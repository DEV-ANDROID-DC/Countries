package com.meg.countries.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.meg.countries.model.CountriesService
import com.meg.countries.model.Country
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import io.reactivex.observers.DisposableSingleObserver
import io.reactivex.schedulers.Schedulers

class ListViewModel : ViewModel() {

    private val countriesService = CountriesService()
    private val disposable = CompositeDisposable()

    val _countries = MutableLiveData<List<Country>>()
    val countries : LiveData<List<Country>>
        get() = _countries
    val _countryLoadError = MutableLiveData<Boolean>()
    val countryLoadError : LiveData<Boolean>
        get() = _countryLoadError
    val _loading = MutableLiveData<Boolean>()
    val loading : LiveData<Boolean>
        get() = _loading
    fun refresh() {
        fetchCountries()
    }

    private fun fetchCountries() {
        _loading.value = true
        disposable.add(
            countriesService.getCountries()
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(object : DisposableSingleObserver<List<Country>> () {
                    override fun onSuccess(value: List<Country>?) {
                        _countries.value = value
                        _countryLoadError.value = false
                        _loading.value = false
                    }

                    override fun onError(e: Throwable?) {
                        _countryLoadError.value = true
                        _loading.value = false
                    }

                })
        )
    }

    override fun onCleared() {
        super.onCleared()
        disposable.clear()
    }
}