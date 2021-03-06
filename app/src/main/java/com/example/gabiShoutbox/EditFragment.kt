package com.example.gabiShoutbox

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class EditFragment : Fragment() {

    private lateinit var jsonPlaceholderAPI: JsonPlaceholderAPI
    private lateinit var infoToast: Toast
    private val baseUrl: String = "http://tgryl.pl/"
    private lateinit var loginx: String
    private lateinit var button: Button
    private lateinit var textLogin: TextView
    private lateinit var textDate: TextView
    private lateinit var textTime: TextView
    private lateinit var editTextContent: EditText
    private lateinit var deleteButton: ImageButton

    private lateinit var login: String
    private lateinit var date: String
    private lateinit var content: String
    private lateinit var id: String

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val root = inflater.inflate(R.layout.fragment_edit, container, false)

        editTextContent = root.findViewById(R.id.editText)
        textLogin = root.findViewById(R.id.loginEditTextView)
        textDate = root.findViewById(R.id.dateEditTextView)
        textTime = root.findViewById(R.id.timeEditTextView)
        button = root.findViewById(R.id.editButton)
        deleteButton=root.findViewById(R.id.delButton)

        login = arguments?.getString("login").toString()
        date = arguments?.getString("date_hour").toString()
        id = arguments?.getString("id")!!.toString()
        content = arguments?.getString("content").toString()

        textLogin.text = login
        textDate.text = date.subSequence(0, 10)
        textTime.text = date.subSequence(11, 19)
        editTextContent.setText(content)



        val retrofit = Retrofit.Builder().baseUrl(baseUrl)
            .addConverterFactory(
                GsonConverterFactory
                    .create()
            )
            .build()
        jsonPlaceholderAPI = retrofit.create(JsonPlaceholderAPI::class.java)

        deleteButton.setOnClickListener {
            val prefs =
                requireActivity().getSharedPreferences("shared preferences", Context.MODE_PRIVATE)
            loginx= prefs.getString("login","").toString();
            if (networkConnection()) {
                if (loginx == login) {
                    deleteData(id)
                    makeToast("Message deleted.")
                    val fragment: Fragment = ShoutboxFragment()
                    val fragmentManager: FragmentManager? = fragmentManager
                    fragmentManager?.beginTransaction()
                        ?.replace(R.id.nav_host_fragment, fragment)
                        ?.commit()
                } else {
                    makeToast("You can only delete your messages.")
                    val fragment: Fragment = ShoutboxFragment()
                    val fragmentManager: FragmentManager? = fragmentManager
                    fragmentManager?.beginTransaction()
                        ?.replace(R.id.nav_host_fragment, fragment)
                        ?.commit()

                }
            }
            else{
                makeToast("No internet connection.")
            }
        }
        button.setOnClickListener {
            val prefs =
                requireActivity().getSharedPreferences("shared preferences", Context.MODE_PRIVATE)
            loginx= prefs.getString("login","").toString();
            if (networkConnection()) {
                if (loginx == login) {
                    content = editTextContent.text.toString()
                    putData()
                    makeToast("Message edited.")
                    val bundle = Bundle()
                    bundle.putString("login", login)
                    val fragment: Fragment = ShoutboxFragment()
                    fragment.arguments = bundle
                    val fragmentManager: FragmentManager? = fragmentManager
                    fragmentManager?.beginTransaction()
                        ?.replace(R.id.nav_host_fragment, fragment)
                        ?.commit()
                } else {
                    makeToast("You can only edit your own messages")
                    val fragment: Fragment = ShoutboxFragment()
                    val fragmentManager: FragmentManager? = fragmentManager
                    fragmentManager?.beginTransaction()
                        ?.replace(R.id.nav_host_fragment, fragment)
                        ?.commit()
                }
            }
            else{
                makeToast("No internet connection.")
            }

        }

        return root
    }

    private fun putData() {
        val message = Message(login, content)
        val call = jsonPlaceholderAPI.createPut(id, message)
        call.enqueue(object : Callback<Message> {
            override fun onResponse(
                call: Call<Message>,
                response: Response<Message>
            ) {
                if (!response.isSuccessful) {
                    println("Code: " + response.code())
                    return
                }

            }
            override fun onFailure(
                call: Call<Message>,
                t: Throwable
            ) {
            }
        })
    }

    fun makeToast(myToastText: String) {
        infoToast = Toast.makeText(
            context,
            myToastText,
            Toast.LENGTH_SHORT
        )
        infoToast.setGravity(Gravity.TOP, 0, 200)
        infoToast.show()
    }

    private fun deleteData(id: String) {
        val call = jsonPlaceholderAPI.createDelete(id)
        call.enqueue(object : Callback<Message> {
            override fun onResponse(
                call: Call<Message>,
                response: Response<Message>
            ) {
                if (!response.isSuccessful) {
                    println("Code: " + response.code())
                    return
                }
            }
            override fun onFailure(
                call: Call<Message>,
                t: Throwable
            ) {
                println(t.message)
            }
        })
    }


    fun networkConnection(): Boolean {
        val connectivityManager =
            context?.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        if (connectivityManager != null) {
            val capabilities =
                connectivityManager.getNetworkCapabilities(connectivityManager.activeNetwork)
            if (capabilities != null) {
                if (capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR)) {
                    Log.i("Internet", "NetworkCapabilities.TRANSPORT_CELLULAR")
                    return true
                } else if (capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI)) {
                    Log.i("Internet", "NetworkCapabilities.TRANSPORT_WIFI")
                    return true
                } else if (capabilities.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET)) {
                    Log.i("Internet", "NetworkCapabilities.TRANSPORT_ETHERNET")
                    return true
                }
            }
        }
        return false
    }

}