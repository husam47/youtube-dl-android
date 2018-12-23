package com.youtubedl.ui.main.home

import android.databinding.*
import com.youtubedl.data.local.room.entity.PageInfo
import com.youtubedl.data.repository.TopPagesRepository
import com.youtubedl.ui.main.base.BaseViewModel
import com.youtubedl.util.SingleLiveEvent
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import javax.inject.Inject

/**
 * Created by cuongpm on 12/7/18.
 */

class BrowserViewModel @Inject constructor(
    private val topPagesRepository: TopPagesRepository
) : BaseViewModel() {

    private var disposable: Disposable? = null

    val textInput = ObservableField<String>("")
    val isFocus = ObservableBoolean(false)
    val changeFocusEvent = SingleLiveEvent<Boolean>()

    val pageUrl = ObservableField<String>("")
    val isShowPage = ObservableBoolean(false)
    val javaScriptEnabled = ObservableBoolean(true)
    val javaScripInterface = ObservableField<String>("browser")

    val isShowProgress = ObservableBoolean(false)
    val progress = ObservableInt(0)

    val isShowFabBtn = ObservableBoolean(false)

    val listPages: ObservableList<PageInfo> = ObservableArrayList()

    val pressBackBtnEvent = SingleLiveEvent<Void>()

    override fun start() {
        getTopPages()
    }

    override fun stop() {
        disposable?.let { it -> if (!it.isDisposed) it.dispose() }
    }

    fun loadPage(url: String) {
        pageUrl.set(url)
        isShowPage.set(true)
        changeFocus(false)
    }

    fun changeFocus(isFocus: Boolean) {
        this.isFocus.set(isFocus)
        changeFocusEvent.value = isFocus
    }

    fun startPage(url: String) {
        textInput.set(url)
        isShowPage.set(true)
        isShowProgress.set(true)
        isShowFabBtn.set(true)
    }

    fun finishPage(url: String) {
        textInput.set(url)
        isShowProgress.set(false)
    }

    private fun getTopPages() {
        disposable = topPagesRepository.getTopPages()
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({ list ->
                with(listPages) {
                    clear()
                    addAll(list)
                }
            }, { error ->
                error.printStackTrace()
            })
    }


}