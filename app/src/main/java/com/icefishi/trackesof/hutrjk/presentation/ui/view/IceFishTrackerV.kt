package com.icefishi.trackesof.hutrjk.presentation.ui.view

import android.content.DialogInterface
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.CookieManager
import android.webkit.ValueCallback
import android.widget.FrameLayout
import androidx.activity.OnBackPressedCallback
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.icefishi.trackesof.hutrjk.presentation.app.IceFishTrackerApplication
import com.icefishi.trackesof.hutrjk.presentation.ui.load.IceFishTrackerLoadFragment
import org.koin.android.ext.android.inject

class IceFishTrackerV : Fragment(){

    private lateinit var iceFishTrackerPhoto: Uri
    private var iceFishTrackerFilePathFromChrome: ValueCallback<Array<Uri>>? = null

    private val iceFishTrackerTakeFile: ActivityResultLauncher<PickVisualMediaRequest> = registerForActivityResult(ActivityResultContracts.PickVisualMedia()) {
        iceFishTrackerFilePathFromChrome?.onReceiveValue(arrayOf(it ?: Uri.EMPTY))
        iceFishTrackerFilePathFromChrome = null
    }

    private val iceFishTrackerTakePhoto: ActivityResultLauncher<Uri> = registerForActivityResult(ActivityResultContracts.TakePicture()) {
        if (it) {
            iceFishTrackerFilePathFromChrome?.onReceiveValue(arrayOf(iceFishTrackerPhoto))
            iceFishTrackerFilePathFromChrome = null
        } else {
            iceFishTrackerFilePathFromChrome?.onReceiveValue(null)
            iceFishTrackerFilePathFromChrome = null
        }
    }

    private val iceFishTrackerDataStore by activityViewModels<IceFishTrackerDataStore>()


    private val iceFishTrackerViFun by inject<IceFishTrackerViFun>()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d(IceFishTrackerApplication.ICE_FISH_TRACKER_MAIN_TAG, "Fragment onCreate")
        CookieManager.getInstance().setAcceptCookie(true)
        requireActivity().onBackPressedDispatcher.addCallback(this,
            object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    if (iceFishTrackerDataStore.iceFishTrackerView.canGoBack()) {
                        iceFishTrackerDataStore.iceFishTrackerView.goBack()
                        Log.d(IceFishTrackerApplication.ICE_FISH_TRACKER_MAIN_TAG, "WebView can go back")
                    } else if (iceFishTrackerDataStore.iceFishTrackerViList.size > 1) {
                        Log.d(IceFishTrackerApplication.ICE_FISH_TRACKER_MAIN_TAG, "WebView can`t go back")
                        iceFishTrackerDataStore.iceFishTrackerViList.removeAt(iceFishTrackerDataStore.iceFishTrackerViList.lastIndex)
                        Log.d(IceFishTrackerApplication.ICE_FISH_TRACKER_MAIN_TAG, "WebView list size ${iceFishTrackerDataStore.iceFishTrackerViList.size}")
                        iceFishTrackerDataStore.iceFishTrackerView.destroy()
                        val previousWebView = iceFishTrackerDataStore.iceFishTrackerViList.last()
                        iceFishTrackerAttachWebViewToContainer(previousWebView)
                        iceFishTrackerDataStore.iceFishTrackerView = previousWebView
                    }
                }

            })
    }


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        if (iceFishTrackerDataStore.iceFishTrackerIsFirstCreate) {
            iceFishTrackerDataStore.iceFishTrackerIsFirstCreate = false
            iceFishTrackerDataStore.iceFishTrackerContainerView = FrameLayout(requireContext()).apply {
                layoutParams = ViewGroup.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.MATCH_PARENT
                )
                id = View.generateViewId()
            }
            return iceFishTrackerDataStore.iceFishTrackerContainerView
        } else {
            return iceFishTrackerDataStore.iceFishTrackerContainerView
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        Log.d(IceFishTrackerApplication.ICE_FISH_TRACKER_MAIN_TAG, "onViewCreated")
        if (iceFishTrackerDataStore.iceFishTrackerViList.isEmpty()) {
            iceFishTrackerDataStore.iceFishTrackerView = IceFishTrackerVi(requireContext(), object :
                IceFishTrackerCallBack {
                override fun iceFishTrackerHandleCreateWebWindowRequest(iceFishTrackerVi: IceFishTrackerVi) {
                    iceFishTrackerDataStore.iceFishTrackerViList.add(iceFishTrackerVi)
                    Log.d(IceFishTrackerApplication.ICE_FISH_TRACKER_MAIN_TAG, "WebView list size = ${iceFishTrackerDataStore.iceFishTrackerViList.size}")
                    Log.d(IceFishTrackerApplication.ICE_FISH_TRACKER_MAIN_TAG, "CreateWebWindowRequest")
                    iceFishTrackerDataStore.iceFishTrackerView = iceFishTrackerVi
                    iceFishTrackerVi.iceFishTrackerSetFileChooserHandler { callback ->
                        iceFishTrackerHandleFileChooser(callback)
                    }
                    iceFishTrackerAttachWebViewToContainer(iceFishTrackerVi)
                }

            }, iceFishTrackerWindow = requireActivity().window).apply {
                iceFishTrackerSetFileChooserHandler { callback ->
                    iceFishTrackerHandleFileChooser(callback)
                }
            }
            iceFishTrackerDataStore.iceFishTrackerView.iceFishTrackerFLoad(arguments?.getString(
                IceFishTrackerLoadFragment.ICE_FISH_TRACKER_D) ?: "")
//            ejvview.fLoad("www.google.com")
            iceFishTrackerDataStore.iceFishTrackerViList.add(iceFishTrackerDataStore.iceFishTrackerView)
            iceFishTrackerAttachWebViewToContainer(iceFishTrackerDataStore.iceFishTrackerView)
        } else {
            iceFishTrackerDataStore.iceFishTrackerViList.forEach { webView ->
                webView.iceFishTrackerSetFileChooserHandler { callback ->
                    iceFishTrackerHandleFileChooser(callback)
                }
            }
            iceFishTrackerDataStore.iceFishTrackerView = iceFishTrackerDataStore.iceFishTrackerViList.last()

            iceFishTrackerAttachWebViewToContainer(iceFishTrackerDataStore.iceFishTrackerView)
        }
        Log.d(IceFishTrackerApplication.ICE_FISH_TRACKER_MAIN_TAG, "WebView list size = ${iceFishTrackerDataStore.iceFishTrackerViList.size}")
    }

    private fun iceFishTrackerHandleFileChooser(callback: ValueCallback<Array<Uri>>?) {
        Log.d(IceFishTrackerApplication.ICE_FISH_TRACKER_MAIN_TAG, "handleFileChooser called, callback: ${callback != null}")

        iceFishTrackerFilePathFromChrome = callback

        val listItems: Array<out String> = arrayOf("Select from file", "To make a photo")
        val listener = DialogInterface.OnClickListener { _, which ->
            when (which) {
                0 -> {
                    Log.d(IceFishTrackerApplication.ICE_FISH_TRACKER_MAIN_TAG, "Launching file picker")
                    iceFishTrackerTakeFile.launch(
                        PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                    )
                }
                1 -> {
                    Log.d(IceFishTrackerApplication.ICE_FISH_TRACKER_MAIN_TAG, "Launching camera")
                    iceFishTrackerPhoto = iceFishTrackerViFun.iceFishTrackerSavePhoto()
                    iceFishTrackerTakePhoto.launch(iceFishTrackerPhoto)
                }
            }
        }

        AlertDialog.Builder(requireContext())
            .setTitle("Choose a method")
            .setItems(listItems, listener)
            .setCancelable(true)
            .setOnCancelListener {
                Log.d(IceFishTrackerApplication.ICE_FISH_TRACKER_MAIN_TAG, "File chooser canceled")
                callback?.onReceiveValue(null)
                iceFishTrackerFilePathFromChrome = null
            }
            .create()
            .show()
    }

    private fun iceFishTrackerAttachWebViewToContainer(w: IceFishTrackerVi) {
        iceFishTrackerDataStore.iceFishTrackerContainerView.post {
            (w.parent as? ViewGroup)?.removeView(w)
            iceFishTrackerDataStore.iceFishTrackerContainerView.removeAllViews()
            iceFishTrackerDataStore.iceFishTrackerContainerView.addView(w)
        }
    }


}