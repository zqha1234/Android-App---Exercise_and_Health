package com.example.qiuhao_zheng_myruns5.history

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ListView
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.example.qiuhao_zheng_myruns5.R
import com.example.qiuhao_zheng_myruns5.databases.DataViewModelFactory
import com.example.qiuhao_zheng_myruns5.databases.DatabaseRepository
import com.example.qiuhao_zheng_myruns5.databases.HealthData
import com.example.qiuhao_zheng_myruns5.databases.HealthDataViewModel
import com.example.qiuhao_zheng_myruns5.databases.HealthDatabase


class History : Fragment() {
    private lateinit var dataViewModel: HealthDataViewModel
    private lateinit var listView: ListView
    private lateinit var arrayList: ArrayList<HealthData>
    private lateinit var adapter: MyListAdapter

    private lateinit var database: HealthDatabase
    private lateinit var repository: DatabaseRepository
    private lateinit var viewModelFactory: DataViewModelFactory

    private lateinit var sharedPreferences: SharedPreferences
    private val preferenceChangeListener = SharedPreferences.OnSharedPreferenceChangeListener { _, key ->
        if (key == "unitPrefer") {
//            println("qz: test unit change") // test use only
            adapter.notifyDataSetChanged()
            listView.invalidateViews()
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {

        val view = inflater.inflate(R.layout.history, container, false)
        // initialize listview
        listView = view?.findViewById(R.id.listView)!!
        arrayList = ArrayList()
        adapter = MyListAdapter(requireActivity(), arrayList)
        listView.adapter = adapter


        database = HealthDatabase.getInstance(requireContext())
        repository = DatabaseRepository(database.databaseDao)
        viewModelFactory = DataViewModelFactory(repository)
        dataViewModel = ViewModelProvider(requireActivity(), viewModelFactory)[HealthDataViewModel::class.java]

        dataViewModel.allCommentsLiveData.observe(requireActivity(), Observer { it ->
            adapter.replace(it)
            adapter.notifyDataSetChanged() // notify the data change
            listView.invalidateViews() // refresh the view
        })

        sharedPreferences = requireActivity().getSharedPreferences("AppPreferences", Context.MODE_PRIVATE)
        sharedPreferences.registerOnSharedPreferenceChangeListener(preferenceChangeListener)

//        return inflater.inflate(R.layout.history, container, false)
        return view
    }
}