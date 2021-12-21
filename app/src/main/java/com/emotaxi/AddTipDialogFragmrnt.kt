package com.emotaxi

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import com.emotaxi.adapter.CardListAdapter
import com.emotaxi.model.CardListModel
import com.emotaxi.retrofit.BackEndApi
import com.emotaxi.retrofit.Constant
import com.emotaxi.retrofit.WebServiceClient
import com.emotaxi.widget.DataManager
import com.emotaxi.widget.SessionManager
import kotlinx.android.synthetic.main.fragment_add_tip_dialog_fragmrnt.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"


class AddTipDialogFragmrnt : DialogFragment(), CardListAdapter.SetOnCloseListener {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null
    var cardListAdapter: CardListAdapter? = null
    var cardArrayList  = ArrayList<CardListModel.Cardinfo>()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_add_tip_dialog_fragmrnt, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        getCardOnStripe()
        rd_group.setOnCheckedChangeListener { group, checkedId ->
            when (checkedId) {
                R.id.rd_btn_first -> {
             Toast.makeText(requireContext(),"First Button", Toast.LENGTH_SHORT).show()
                }
                R.id.rd_btn_second -> {
                    Toast.makeText(requireContext(),"Second Button", Toast.LENGTH_SHORT).show()
                }
                R.id.rd_btn_third -> {
                    Toast.makeText(requireContext(),"Third Button", Toast.LENGTH_SHORT).show()
                }
                R.id.rd_btn_four -> {
                    Toast.makeText(requireContext(),"Four Button", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    companion object {
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            AddTipDialogFragmrnt().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }

    fun getCardOnStripe() {
        var hashmap = HashMap<String, String>()
        hashmap["user_id"] = DataManager.dataManager.getSignUpModel(requireContext()).data[0].userId
        hashmap["customer_id"] =
            SessionManager.readString(requireContext(), Constant.CustomerId, "").toString()
        var hashmapheader = HashMap<String, String>()
        hashmapheader["version"] = "1"
        hashmapheader["device_type"] = "android"
        WebServiceClient.client1.create(BackEndApi::class.java)
            .getcard(hashmapheader, hashmap).enqueue(object : Callback<CardListModel> {
                override fun onFailure(call: Call<CardListModel>, t: Throwable) {
                }

                override fun onResponse(
                    call: Call<CardListModel>,
                    response: Response<CardListModel>
                ) {
                    if (response.body()?.data != null) {
                        if (response.body()?.data!![0].cardinfo != null &&
                            response.body()?.data!![0].cardinfo.size > 0
                        ) {
                              var cardArrayList = ArrayList<CardListModel.Cardinfo>()
                            cardArrayList?.addAll(response.body()?.data!![0].cardinfo)
                            cardListAdapter = CardListAdapter(
                                requireContext(),
                                response.body()?.data!![0].cardinfo as ArrayList<CardListModel.Cardinfo>,
                                this@AddTipDialogFragmrnt
                            )
                            rv_card.adapter = cardListAdapter
                        }
                    } else {
                        Toast.makeText(
                            requireContext(), response.body()?.message,
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }

            })
    }

    override fun onCloseListener(cardinfo: CardListModel.Cardinfo, clickOnView: Boolean, position : Int) {
        if (clickOnView) {
           // removecardCardOnStripe(cardinfo)
        } else {

        }
    }
}