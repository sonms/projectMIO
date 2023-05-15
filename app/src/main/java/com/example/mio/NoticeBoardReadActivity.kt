package com.example.mio

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.mio.Model.PostData
import com.example.mio.databinding.ActivityNoticeBoardBinding
import com.example.mio.databinding.ActivityNoticeBoardReadBinding

class NoticeBoardReadActivity : AppCompatActivity() {
    private lateinit var nbrBinding : ActivityNoticeBoardReadBinding
    private var temp : PostData? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        nbrBinding = ActivityNoticeBoardReadBinding.inflate(layoutInflater)
        setContentView(nbrBinding.root)

        val type = intent.getStringExtra("type")

        if (type.equals("READ")) {
            temp = intent.getSerializableExtra("postItem") as PostData?
            nbrBinding.readContentText.text = temp!!.postContent
        }




    }
}