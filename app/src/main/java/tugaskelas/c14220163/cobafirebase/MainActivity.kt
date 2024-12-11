package tugaskelas.c14220163.cobafirebase

import android.os.Bundle
import android.util.Log
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.ListView
import android.widget.SimpleAdapter
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.firebase.Firebase
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.firestore

class MainActivity : AppCompatActivity() {
    //inisialisasi firebase
    val db = Firebase.firestore

    //inisialisasi variabel global untuk arraylist
    var DataProvinsi = ArrayList<daftarProvinsi>()
//    lateinit var lvAdapter: ArrayAdapter<daftarProvinsi>
    lateinit var lvadapter: SimpleAdapter

    //inisialisasi variabel global untuk edittext provinsi dan ibukota
    lateinit var _etProvinsi: EditText
    lateinit var _etIbukota: EditText

    var data : MutableList<Map<String, String>> = ArrayList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.daftar_provinsi)

        //isi dari variabel di atas
        _etProvinsi = findViewById(R.id.etProvinsi)
        _etIbukota = findViewById(R.id.etIbukota)
        val _btSimpan = findViewById<Button>(R.id.btSimpan)
        val _lvData = findViewById<ListView>(R.id.lvData)

//        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
//            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
//            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
//            insets
//        }

        //adapter
        lvadapter = SimpleAdapter(
            this,
            data,
            android.R.layout.simple_list_item_2,
            arrayOf("Pro", "Ibu"),
            intArrayOf(
                android.R.id.text1, android.R.id.text2
            )
        )
        _lvData.adapter = lvadapter

        _btSimpan.setOnClickListener {
            tambahData(db, _etProvinsi.text.toString(), _etIbukota.text.toString())
        }
        readData(db)
    }

    //function tambah data
    fun tambahData(db: FirebaseFirestore, Provinsi: String, Ibukota: String) {
        val dataBaru = daftarProvinsi(Provinsi, Ibukota)
        db.collection("tbProvinsi")
            .add(dataBaru)
            .addOnSuccessListener {
                _etProvinsi.setText("")
                _etIbukota.setText("")
                Log.d("Firebase", "Data berhasil disimpan")
                readData(db)
            }
            .addOnFailureListener {
                Log.d("Firebase", it.message.toString())
            }
    }

    //menampilkan data
    fun readData(db: FirebaseFirestore) {
        db.collection("tbProvinsi").get()
            .addOnSuccessListener {
                result ->
                DataProvinsi.clear()
                for (document in result) {
                    val readData = daftarProvinsi(
                        document.data["provinsi"].toString(),
                        document.data["ibukota"].toString()
                    )
                    DataProvinsi.add(readData)
                }
                data.clear()
                DataProvinsi.forEach {
                    val dt: MutableMap<String, String> = HashMap(2)
                    dt["Pro"] = it.provinsi
                    dt["Ibu"] = it.ibukota
                    data.add(dt)
                }
                lvadapter.notifyDataSetChanged()
            }
            .addOnFailureListener {
                Log.d("Firebase", it.message.toString())
            }
    }
}
