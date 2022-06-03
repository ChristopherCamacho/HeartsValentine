package com.example.heartsvalentine

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ListView
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.heartsvalentine.databinding.FragmentNewFeaturesBinding
import com.example.heartsvalentine.viewModels.NewFeaturesViewModel

/**
 * A simple [Fragment] subclass.
 * Use the [NewFeatures.newInstance] factory method to
 * create an instance of this fragment.
 */
class NewFeatures : Fragment() {
    private var fragmentActivityContext: FragmentActivity? = null
    private var sKUDetailsList: ListView? = null
    private var newFeaturesViewModel: NewFeaturesViewModel? = null
    private var binding: FragmentNewFeaturesBinding? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
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
        binding = FragmentNewFeaturesBinding.inflate(layoutInflater)
        container?.removeAllViews()
        val view = binding!!.root

        val button = view.findViewById<View>(R.id.backButton)
        button.setOnClickListener { navigateToSettingsFragment() }

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?)
    {
        super.onViewCreated(view, savedInstanceState)

        val newFeaturesViewModelFactory: NewFeaturesViewModel.NewFeaturesViewModelFactory =
        NewFeaturesViewModel.NewFeaturesViewModelFactory(
            (requireActivity().application as HeartsValentineApplication).appContainer.newFeaturesRepository
            )
        newFeaturesViewModel = ViewModelProvider(this, newFeaturesViewModelFactory)
            .get(NewFeaturesViewModel::class.java)

        binding?.nfvm = newFeaturesViewModel
        binding!!.inappInventory.setLayoutManager(LinearLayoutManager(context))

        val app = activity

        if (app != null){
            val nfr =
                (app.application as HeartsValentineApplication).appContainer.newFeaturesRepository
            val skuArrayList = nfr.getSKUArray().toCollection(ArrayList())

            binding!!.inappInventory.setAdapter(
                NewFeatureListAdapter(
                    requireContext(),
                    skuArrayList,
                    newFeaturesViewModel!!,
                    this,
                    nfr,
                    app
                ))
        }
    }

    private fun navigateToSettingsFragment() {
        val fragment: Fragment = Settings()

        if (fragmentActivityContext != null) {
            val fragmentManager: FragmentManager =
                fragmentActivityContext!!.supportFragmentManager
            fragmentManager.beginTransaction()
                .replace(R.id.settings_frame, fragment)
                .setReorderingAllowed(true)
                .commit()
        }
    }

    fun showInfoPopUp(skuDetails: NewFeaturesViewModel.SkuDetails) {
    }

    fun makePurchase(sku: String) {
        activity?.let { newFeaturesViewModel?.buySku(it, sku) }
    }

    companion object {
        @JvmStatic
        fun newInstance() =
            NewFeatures().apply {
                arguments = Bundle().apply {
                }
            }
    }
}