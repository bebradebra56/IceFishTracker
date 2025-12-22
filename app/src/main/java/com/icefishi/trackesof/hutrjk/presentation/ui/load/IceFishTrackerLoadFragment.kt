package com.icefishi.trackesof.hutrjk.presentation.ui.load

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.view.View
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import com.icefishi.trackesof.MainActivity
import com.icefishi.trackesof.R
import com.icefishi.trackesof.databinding.FragmentLoadIceFishTrackerBinding
import com.icefishi.trackesof.hutrjk.data.shar.IceFishTrackerSharedPreference
import kotlinx.coroutines.launch
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.viewModel


class IceFishTrackerLoadFragment : Fragment(R.layout.fragment_load_ice_fish_tracker) {
    private lateinit var iceFishTrackerLoadBinding: FragmentLoadIceFishTrackerBinding

    private val iceFishTrackerLoadViewModel by viewModel<IceFishTrackerLoadViewModel>()

    private val iceFishTrackerSharedPreference by inject<IceFishTrackerSharedPreference>()

    private var iceFishTrackerUrl = ""

    private val iceFishTrackerRequestNotificationPermission = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            iceFishTrackerNavigateToSuccess(iceFishTrackerUrl)
        } else {
            if (shouldShowRequestPermissionRationale(Manifest.permission.POST_NOTIFICATIONS)) {
                iceFishTrackerSharedPreference.iceFishTrackerNotificationRequest =
                    (System.currentTimeMillis() / 1000) + 259200
                iceFishTrackerNavigateToSuccess(iceFishTrackerUrl)
            } else {
                iceFishTrackerNavigateToSuccess(iceFishTrackerUrl)
            }
        }
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        iceFishTrackerLoadBinding = FragmentLoadIceFishTrackerBinding.bind(view)

        iceFishTrackerLoadBinding.iceFishTrackerGrandButton.setOnClickListener {
            val iceFishTrackerPermission = Manifest.permission.POST_NOTIFICATIONS
            iceFishTrackerRequestNotificationPermission.launch(iceFishTrackerPermission)
            iceFishTrackerSharedPreference.iceFishTrackerNotificationRequestedBefore = true
        }

        iceFishTrackerLoadBinding.iceFishTrackerSkipButton.setOnClickListener {
            iceFishTrackerSharedPreference.iceFishTrackerNotificationRequest =
                (System.currentTimeMillis() / 1000) + 259200
            iceFishTrackerNavigateToSuccess(iceFishTrackerUrl)
        }

        lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                iceFishTrackerLoadViewModel.iceFishTrackerHomeScreenState.collect {
                    when (it) {
                        is IceFishTrackerLoadViewModel.IceFishTrackerHomeScreenState.IceFishTrackerLoading -> {

                        }

                        is IceFishTrackerLoadViewModel.IceFishTrackerHomeScreenState.IceFishTrackerError -> {
                            requireActivity().startActivity(
                                Intent(
                                    requireContext(),
                                    MainActivity::class.java
                                )
                            )
                            requireActivity().finish()
                        }

                        is IceFishTrackerLoadViewModel.IceFishTrackerHomeScreenState.IceFishTrackerSuccess -> {
                            if (Build.VERSION.SDK_INT > Build.VERSION_CODES.S_V2) {
                                val iceFishTrackerPermission = Manifest.permission.POST_NOTIFICATIONS
                                val iceFishTrackerPermissionRequestedBefore = iceFishTrackerSharedPreference.iceFishTrackerNotificationRequestedBefore

                                if (ContextCompat.checkSelfPermission(requireContext(), iceFishTrackerPermission) == PackageManager.PERMISSION_GRANTED) {
                                    iceFishTrackerNavigateToSuccess(it.data)
                                } else if (!iceFishTrackerPermissionRequestedBefore && (System.currentTimeMillis() / 1000 > iceFishTrackerSharedPreference.iceFishTrackerNotificationRequest)) {
                                    // первый раз — показываем UI для запроса
                                    iceFishTrackerLoadBinding.iceFishTrackerNotiGroup.visibility = View.VISIBLE
                                    iceFishTrackerLoadBinding.iceFishTrackerLoadingGroup.visibility = View.GONE
                                    iceFishTrackerUrl = it.data
                                } else if (shouldShowRequestPermissionRationale(iceFishTrackerPermission)) {
                                    // временный отказ — через 3 дня можно показать
                                    if (System.currentTimeMillis() / 1000 > iceFishTrackerSharedPreference.iceFishTrackerNotificationRequest) {
                                        iceFishTrackerLoadBinding.iceFishTrackerNotiGroup.visibility = View.VISIBLE
                                        iceFishTrackerLoadBinding.iceFishTrackerLoadingGroup.visibility = View.GONE
                                        iceFishTrackerUrl = it.data
                                    } else {
                                        iceFishTrackerNavigateToSuccess(it.data)
                                    }
                                } else {
                                    // навсегда отклонено — просто пропускаем
                                    iceFishTrackerNavigateToSuccess(it.data)
                                }
                            } else {
                                iceFishTrackerNavigateToSuccess(it.data)
                            }
                        }

                        IceFishTrackerLoadViewModel.IceFishTrackerHomeScreenState.IceFishTrackerNotInternet -> {
                            iceFishTrackerLoadBinding.iceFishTrackerStateGroup.visibility = View.VISIBLE
                            iceFishTrackerLoadBinding.iceFishTrackerLoadingGroup.visibility = View.GONE
                        }
                    }
                }
            }
        }
    }


    private fun iceFishTrackerNavigateToSuccess(data: String) {
        findNavController().navigate(
            R.id.action_iceFishTrackerLoadFragment_to_iceFishTrackerV,
            bundleOf(ICE_FISH_TRACKER_D to data)
        )
    }

    companion object {
        const val ICE_FISH_TRACKER_D = "iceFishTrackerData"
    }
}