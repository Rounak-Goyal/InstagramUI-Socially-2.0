package com.example.instagramapp.Adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.NonNull
import androidx.recyclerview.widget.RecyclerView
import com.example.instagramapp.Model.Post
import com.example.instagramapp.Model.User
import com.example.instagramapp.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.squareup.picasso.Picasso
import de.hdodenhof.circleimageview.CircleImageView
import kotlinx.android.synthetic.main.activity_account_settings.*

class PostAdapter (private var mContext: Context,
                   private var mPost: List<Post>,
                   private var isFragment: Boolean = false)
    : RecyclerView.Adapter<PostAdapter.ViewHolder>()
{
    private var firebaseUser: FirebaseUser? = FirebaseAuth.getInstance().currentUser

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PostAdapter.ViewHolder {
        val view = LayoutInflater.from(mContext)
            .inflate(R.layout.post_item_layout, parent,false)
        return PostAdapter.ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return mPost.size
    }

    override fun onBindViewHolder(holder: PostAdapter.ViewHolder, position: Int) {

        val post = mPost[position]

        Picasso.get().load(post.getPostimage()).placeholder(R.drawable.add_image_icon).into(holder.postimage)
        publisherInfo(holder.profileImage,holder.userName,holder.publisher,post.getPublisher())
        holder.discription.text = post.getDescription()

        holder.likeButton.setOnClickListener {

            isLiked(holder.likeButton,holder.likes,post.getPostid().toString())

            if (holder.likeButton.tag?.toString()=="like")
            {
                FirebaseDatabase.getInstance().reference.child("Likes").child(post.getPostid())
                    .child(firebaseUser!!.uid)
                    .setValue(true)
            }
            else
            {
                FirebaseDatabase.getInstance().reference.child("Likes").child(post.getPostid())
                    .child(firebaseUser!!.uid)
                    .removeValue()
            }

        }




    }

    private fun isLiked(likeButton: ImageView,likes: TextView,postid: String) {
        firebaseUser=FirebaseAuth.getInstance().currentUser
        val postRef=FirebaseDatabase.getInstance().reference.child("Likes").child(postid)

        postRef.addValueEventListener(object : ValueEventListener {

            override fun onDataChange(datasnapshot: DataSnapshot) {
                if (datasnapshot.child(firebaseUser!!.uid).exists()) {
                    likeButton.setImageResource(R.drawable.heart_clicked)
                    likeButton.tag = "liked"
                }
                else {
                    likeButton.setImageResource(R.drawable.heart_not_clicked)
                    likeButton.tag = "like"
                }
                //to get the count of Likes
                likes.text = datasnapshot.childrenCount.toString()+" likes"
            }

            override fun onCancelled(error: DatabaseError) {
            }
        })
    }

    class ViewHolder (@NonNull itemView: View) : RecyclerView.ViewHolder(itemView)
    {
        var profileImage: CircleImageView = itemView.findViewById(R.id.user_profile_image_post)
        var postimage: ImageView = itemView.findViewById(R.id.post_image_home)
        var likeButton: ImageView = itemView.findViewById(R.id.post_image_like_btn)
        var commentButton: ImageView = itemView.findViewById(R.id.post_image_comment_btn)
        var saveButton: ImageView = itemView.findViewById(R.id.post_save_comment_btn)
        var userName: TextView = itemView.findViewById(R.id.user_name_post)
        var likes: TextView = itemView.findViewById(R.id.likes)
        var publisher: TextView = itemView.findViewById(R.id.publisher)
        var comments: TextView = itemView.findViewById(R.id.comments)
        var discription: TextView = itemView.findViewById(R.id.description)
    }

    private fun publisherInfo(profileImage: CircleImageView, userName: TextView, publisher: TextView, publisherId: String) {
        val usersRef = FirebaseDatabase.getInstance().reference.child("Users").child(publisherId)
        usersRef.addValueEventListener(object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot)
            {
                if(snapshot.exists()){
                    val user = snapshot.getValue<User>(User::class.java)

                    Picasso.get().load(user!!.getImage()).placeholder(R.drawable.profile)
                        .into(profileImage)
                    userName.text = user!!.getUserName()
                    publisher.text = user!!.getFullName()

                }

            }

            override fun onCancelled(error: DatabaseError) {
            }

        })

    }
}