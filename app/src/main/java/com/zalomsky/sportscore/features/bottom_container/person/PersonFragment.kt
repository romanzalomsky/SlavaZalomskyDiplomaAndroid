package com.zalomsky.sportscore.features.bottom_container.person

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.navigation.findNavController
import com.zalomsky.sportscore.R

class PersonFragment : Fragment() {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val linkToCountry = view.findViewById<Button>(R.id.linkToCountryButton)

        linkToCountry.setOnClickListener {
            it.findNavController().navigate(R.id.action_personFragment_to_countryFragment)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_person, container, false)

        return view
    }
}