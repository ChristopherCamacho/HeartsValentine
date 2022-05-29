package com.example.heartsvalentine

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.LiveData
import androidx.lifecycle.asLiveData
import com.example.heartsvalentine.billing.NewFeaturesRepository

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [NewFeatures.newInstance] factory method to
 * create an instance of this fragment.
 */
class NewFeatures : Fragment() {
    // TODO: Rename and change types of parameters
    private var fragmentActivityContext: FragmentActivity? = null
    private var param1: String? = null
    private var param2: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }

        val app = activity

        if (app != null) {
            val nfr = (app.application as HeartsValentineApplication).appContainer.newFeaturesRepository

            val skuemoji = nfr.isPurchased(NewFeaturesRepository.SKU_EMOJI).asLiveData()

        }
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        fragmentActivityContext = context as FragmentActivity
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        container?.removeAllViews()
        // Inflate the layout for this fragment

        val view = inflater.inflate(R.layout.fragment_new_features, container, false)
        val button = view.findViewById<View>(R.id.backButton)
        button.setOnClickListener { _: View? -> navigateToSettingsFragment() }

        return view
    }

    private fun navigateToSettingsFragment() {
        val fragment: Fragment = Settings()

        if (fragmentActivityContext != null) {
            val fragmentManager: FragmentManager =
                fragmentActivityContext!!.getSupportFragmentManager()
            fragmentManager.beginTransaction()
                .replace(R.id.settings_frame, fragment)
                .setReorderingAllowed(true)
                .commit()
        }
    }











    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment NewFeatures.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            NewFeatures().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}


