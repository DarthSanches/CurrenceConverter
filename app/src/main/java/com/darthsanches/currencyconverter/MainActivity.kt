package com.darthsanches.currencyconverter

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.DefaultItemAnimator
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.Log
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit


class MainActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var viewAdapter: CurrencyListAdapter
    private lateinit var viewManager: RecyclerView.LayoutManager
    private lateinit var api:Endpoints
    private val disposable = CompositeDisposable()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        initApi()
        viewManager = LinearLayoutManager(this)
        viewAdapter = CurrencyListAdapter()

        recyclerView = findViewById<RecyclerView>(R.id.recycler_view).apply {
            setHasFixedSize(true)
            layoutManager = viewManager
            adapter = viewAdapter
            itemAnimator = object : DefaultItemAnimator() {
                override fun onAnimationFinished(viewHolder: RecyclerView.ViewHolder) {
                    if(!isComputingLayout && viewHolder.adapterPosition == 0) {
                        disposable.clear()
                        startPoll()
                        viewAdapter.notifyDataSetChanged()
                    }
                }
            }
        }
        startPoll()
    }

    private fun startPoll() {
        disposable.add(api.getRates(viewAdapter.getBase())
                .subscribeOn(Schedulers.io())
                .repeatWhen { it.delay(1, TimeUnit.SECONDS) }
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    viewAdapter.setData(it)
                }, { Log.e("main", "Something went wrong", it) }))
    }


    private fun initApi(){
        val client = OkHttpClient.Builder()
                .connectTimeout(30, TimeUnit.SECONDS)
                .readTimeout(60, TimeUnit.SECONDS)
                .build()
        val retrofit = Retrofit.Builder()
                .client(client)
                .baseUrl("https://revolut.duckdns.org/")
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJava2CallAdapterFactory.createWithScheduler(Schedulers.io()))
                .build()

        api = retrofit.create(Endpoints::class.java)
    }
}
