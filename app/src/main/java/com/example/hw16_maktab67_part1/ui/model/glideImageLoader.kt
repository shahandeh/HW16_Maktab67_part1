package com.example.hw16_maktab67_part1.ui.model

import android.widget.ImageView
import com.bumptech.glide.Glide

fun ImageView.glide(uri: String){
    Glide.with(this)
        .load(uri)
        .into(this)
}