package com.evanandroid.apps.photodescription.ui.slideshow

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.widget.LinearLayoutCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.evanandroid.apps.photodescription.R
import com.evanandroid.apps.photodescription.ui.gallery.ContentList
import com.evanandroid.apps.photodescription.ui.gallery.GalleryFragment
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.fragment_slideshow.view.*
import java.nio.BufferUnderflowException
import com.evanandroid.apps.photodescription.ui.gallery.GalleryFragment.DetailViewRecyclerViewAdapter as DetailViewRecyclerViewAdapter


class SlideshowFragment : Fragment() {
    var fragmentView : View? = null
    var firestore : FirebaseFirestore? = null
    var uid : String? = null
    var auth : FirebaseAuth? = null

    private lateinit var slideshowViewModel: SlideshowViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        slideshowViewModel =
            ViewModelProviders.of(this).get(SlideshowViewModel::class.java)

        fragmentView = LayoutInflater.from(activity).inflate(R.layout.fragment_slideshow,container,false)
        uid = arguments?.getString("destinationUid")
        firestore = FirebaseFirestore.getInstance()

        auth = FirebaseAuth.getInstance()
        fragmentView?.account_recyclerview?.adapter = UserFragementRecyclerViewAdapter()
        fragmentView?.account_recyclerview?.layoutManager = GridLayoutManager(this.requireActivity(),3)

        return fragmentView
    }

    inner class UserFragementRecyclerViewAdapter : RecyclerView.Adapter<RecyclerView.ViewHolder>(){
        var content : ArrayList<ContentList> = arrayListOf()
        init {
            //내가 입력한 데이터만 불러올 수 있도록 하는 코드
            firestore?.collection("images")?.whereEqualTo("uid",uid)?.addSnapshotListener { querySnapshot, firebaseFirestoreException ->
                //프로그램의 안전함을 위해 추가시키는 코드
                if(querySnapshot == null) return@addSnapshotListener
                //데이터를 받아주는 코드
                for (snapshot in querySnapshot.documents){
                    content.add(snapshot.toObject(ContentList::class.java)!!)
                }
                notifyDataSetChanged()
            }

        }
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
            //바로 view를 불러오는 것이 아닌 폭의 값부터 불러오는 코드
            //아래 두 코드를 통해 폭에 3분에 1 크기의 부분을 가져올 수 있다.
            var width = resources.displayMetrics.widthPixels / 3
            var imageview = ImageView(parent.context)
            imageview.layoutParams = LinearLayoutCompat.LayoutParams(width,width)
            return CustomViewHolder(imageview)
        }

        inner class CustomViewHolder(var imageview: ImageView) : RecyclerView.ViewHolder(imageview) {

        }

        override fun getItemCount(): Int {
            return content.size
        }

        override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
          //이미지를 불러오는 부분
            var imageview = (holder as CustomViewHolder).imageview
            Glide.with(holder.itemView.context).load(content[position].imageUrl).apply(RequestOptions().centerCrop()).into(imageview)

    }
    }
}