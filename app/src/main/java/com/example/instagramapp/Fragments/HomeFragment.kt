package com.example.instagramapp.Fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.instagramapp.Adapter.PostAdapter
import com.example.instagramapp.Adapter.UserAdapter
import com.example.instagramapp.Model.Post
import com.example.instagramapp.Model.User
import com.example.instagramapp.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class HomeFragment : Fragment() {
    private var recyclerView: RecyclerView? = null
    private var postAdapter: PostAdapter? = null
    private var mPost: MutableList<Post>? = null
    private var followingList: MutableList<Post>? = null


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_home, container, false)


        recyclerView=view.findViewById(R.id.recycler_view_home)
        val linearLayoutManager: LinearLayoutManager = LinearLayoutManager(context)
        linearLayoutManager.reverseLayout=true
        linearLayoutManager.stackFromEnd=true
        recyclerView?.layoutManager=linearLayoutManager

        mPost = ArrayList()
        postAdapter=context?.let { PostAdapter(it,mPost as ArrayList<Post>,true) }
        recyclerView?.adapter=postAdapter

        checkFollowings()


        return view
    }

    private fun checkFollowings() {
        followingList = ArrayList()

        val followingRef = FirebaseDatabase.getInstance().reference.child("Follow")
            .child(FirebaseAuth.getInstance().currentUser!!.uid).child("Following")

        followingRef.addValueEventListener(object : ValueEventListener{
            override fun onDataChange(p0: DataSnapshot)
            {
                if(p0.exists())
                {
                    (followingList as ArrayList<String>).clear()
                    for(snapshot in p0.children)
                    {
                        snapshot.key?.let { (followingList as ArrayList<String>).add(it) }
                    }
                    retrievePosts()
                }
            }

            override fun onCancelled(error: DatabaseError) {
            }

        })
    }

    private fun retrievePosts(){
        val postsRef = FirebaseDatabase.getInstance().reference.child("Posts")

        postsRef.addValueEventListener(object : ValueEventListener{
            override fun onDataChange(p0: DataSnapshot) {
                mPost?.clear()
                for(snapshot in p0.children)
                {
                    val post = snapshot.getValue(Post::class.java)

                    for(userId in (followingList as ArrayList<String>)){

                        if(post!!.getPublisher() == userId){
                            mPost!!.add(post)
                        }

                        postAdapter!!.notifyDataSetChanged()
                    }
                }

            }

            override fun onCancelled(error: DatabaseError) {
            }

        })
    }

}