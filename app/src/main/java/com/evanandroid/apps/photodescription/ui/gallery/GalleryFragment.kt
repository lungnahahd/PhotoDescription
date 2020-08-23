package com.evanandroid.apps.photodescription.ui.gallery

import android.os.Bundle
import android.text.Layout
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.evanandroid.apps.photodescription.R
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.fragment_gallery.*
import kotlinx.android.synthetic.main.fragment_gallery.view.*
import kotlinx.android.synthetic.main.item_show.view.*

class GalleryFragment : Fragment() {
    var firestore : FirebaseFirestore? = null
    private lateinit var galleryViewModel: GalleryViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        firestore = FirebaseFirestore.getInstance()
        galleryViewModel =
            ViewModelProviders.of(this).get(GalleryViewModel::class.java)
        val root = inflater.inflate(R.layout.fragment_gallery, container, false)
        root.detail_view_recyclerview.adapter = DetailViewRecyclerViewAdapter()
        root.detail_view_recyclerview.layoutManager = LinearLayoutManager(activity)
        return root
    }
    inner class DetailViewRecyclerViewAdapter : RecyclerView.Adapter<RecyclerView.ViewHolder>(){
        var contentData : ArrayList<ContentList> = arrayListOf()
        var contentUid : ArrayList<String> = arrayListOf()
        init {
            //데이터베이스에 접근해서 데이터를 받아올 수 있는 코드
            firestore?.collection("images")?.orderBy("time")?.addSnapshotListener { querySnapshot, firebaseFirestoreException ->
                contentData.clear()
                contentUid.clear()
                //스냅 샷에 들어오는 데이터를 읽어주는 부분
                for(snapshot in querySnapshot!!.documents){
                    var item = snapshot.toObject(ContentList::class.java)
                    contentData.add(item!!)
                    contentUid.add(snapshot.id)
                }
                notifyDataSetChanged()
            }
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
            var view = LayoutInflater.from(parent.context).inflate(R.layout.item_show,parent,false)
            return CustomViewHolder(view)
        }

        //메모리를 적게 사용하기 위해 내부 클래스 이용
        inner class CustomViewHolder(view: View) : RecyclerView.ViewHolder(view)

        override fun getItemCount(): Int {
            return contentData.size
        }

        override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
            var viewholder = (holder as CustomViewHolder).itemView

            //UserId를 연결
            viewholder.detail_profile_text.text = contentData!![position].userId
            //Image를 연결
            Glide.with(holder.itemView.context).load(contentData!![position].imageUrl).into(viewholder.detail_image_list)
            //User profile 이미지를 연결
            Glide.with(holder.itemView.context).load(contentData!![position].imageUrl).into(viewholder.detail_profile_image)
            //설명 부분을 연결
            viewholder.detail_explain.text = contentData!![position].explation
        }

    }
}