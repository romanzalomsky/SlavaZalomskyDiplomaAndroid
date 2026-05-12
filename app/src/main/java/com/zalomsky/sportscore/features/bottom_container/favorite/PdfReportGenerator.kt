package com.zalomsky.sportscore.features.bottom_container.favorite

import android.content.ContentValues
import android.content.Context
import android.graphics.Color
import android.graphics.Paint
import android.graphics.pdf.PdfDocument
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import com.zalomsky.sportscore.domain.models.responses.MatchResponseModel
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class PdfReportGenerator(private val context: Context) {

    fun createReport(matches: List<MatchResponseModel>) {
        val document = PdfDocument()
        val titlePaint = Paint().apply {
            color = Color.BLACK
            textSize = 24f
            isFakeBoldText = true
        }
        val textPaint = Paint().apply {
            color = Color.BLACK
            textSize = 12f
        }

        var pageNumber = 1
        var yPosition = 50
        val pageHeight = 842
        val pageWidth = 595
        val margin = 40f
        val lineHeight = 20f

        var pageInfo = PdfDocument.PageInfo.Builder(pageWidth, pageHeight, pageNumber).create()
        var page = document.startPage(pageInfo)
        var canvas = page.canvas

        canvas.drawText("Отчет по избранным матчам", margin, yPosition.toFloat(), titlePaint)
        yPosition += 40

        matches.forEach { match ->
            val matchText = formatMatchDetails(match)

            if (yPosition + lineHeight > pageHeight - margin) {
                document.finishPage(page)
                pageNumber++
                pageInfo = PdfDocument.PageInfo.Builder(pageWidth, pageHeight, pageNumber).create()
                page = document.startPage(pageInfo)
                canvas = page.canvas
                yPosition = margin.toInt() + 10

            }

            matchText.split('\n').forEach { line ->
                canvas.drawText(line, margin, yPosition.toFloat(), textPaint)
                yPosition += lineHeight.toInt()
            }
            yPosition += 10
        }

        document.finishPage(page)

        savePdfFile(document)
    }

    private fun formatMatchDetails(match: MatchResponseModel): String {
        val homeName = match.homeName
        val awayName = match.awayName
        val homeScore = match.homeScore?.toString() ?: "Н/Д"
        val awayScore = match.awayScore?.toString() ?: "Н/Д"
        val leagueName = match.leagueId.ifEmpty { "НЕТ ДАННЫХ" }

        val date = SimpleDateFormat("dd.MM.yyyy HH:mm", Locale.getDefault()).format(Date())

        return """
        ---
        Лига: $leagueName
        Дата: $date
        Матч: $homeName vs $awayName
        Счет: $homeScore:$awayScore
    """.trimIndent()
    }

    private fun savePdfFile(document: PdfDocument) {
        val fileName = "FavoriteMatchesReport_${SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())}.pdf"

        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                val resolver = context.contentResolver
                val contentValues = ContentValues().apply {
                    put(MediaStore.MediaColumns.DISPLAY_NAME, fileName)
                    put(MediaStore.MediaColumns.MIME_TYPE, "application/pdf")
                    put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_DOWNLOADS)
                }

                val uri = resolver.insert(MediaStore.Downloads.EXTERNAL_CONTENT_URI, contentValues)

                uri?.let {
                    resolver.openOutputStream(it)?.use { outputStream ->
                        document.writeTo(outputStream)
                        document.close()
                        Log.d("PdfReportGenerator", "PDF сохранен в Downloads (MediaStore)")
                    } ?: throw IOException("Не удалось открыть выходной поток для URI MediaStore.")
                } ?: throw IOException("Не удалось создать URI для MediaStore.")

            } else {
                val downloadsDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
                downloadsDir.mkdirs()
                val file = File(downloadsDir, fileName)

                FileOutputStream(file).use { fileOutputStream ->
                    document.writeTo(fileOutputStream)
                    document.close()
                    Log.d("PdfReportGenerator", "PDF сохранен в: ${file.absolutePath} (FileOutputStream)")
                }
            }
        } catch (e: Exception) {
            Log.e("PdfReportGenerator", "Критическая ошибка при сохранении PDF", e)

            throw e
        }
    }
}