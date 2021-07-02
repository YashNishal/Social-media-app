package com.example.social_media

import android.content.DialogInterface
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.social_media.dao.PostDao
import com.example.social_media.models.Post
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.Query
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity(), IPostAdapter {

    private lateinit var adapter: PostAdapter
    private lateinit var postDao: PostDao
    private lateinit var mFirebaseAuth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        fab.setOnClickListener {
            val intent = Intent(this, CreatePostActivity::class.java)
            startActivity(intent)
        }

        mFirebaseAuth = FirebaseAuth.getInstance()
        setUpRecyclerView()
    }

    private fun setUpRecyclerView() {
        postDao = PostDao()
        val postsCollections = postDao.postCollections
        val query = postsCollections.orderBy("createdAt", Query.Direction.DESCENDING)
        val recyclerViewOptions = FirestoreRecyclerOptions.Builder<Post>().setQuery(query,Post::class.java).build()


        adapter = PostAdapter(recyclerViewOptions, this)

        recyclerView.adapter = adapter
        recyclerView.layoutManager = LinearLayoutManager(this)
    }

    override fun onStart() {
        super.onStart()
        adapter.startListening()
    }

    override fun onStop() {
        super.onStop()
        adapter.stopListening()
    }

    override fun onLikeClicked(postId: String) {
        postDao.updateLikes(postId)
    }
    // for adding sign out functionality



    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.sign_out_menu,menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if(item.itemId == R.id.menu_sign_out) {
            logOut()
        }
        return super.onOptionsItemSelected(item)
    }


    private fun logOut() {
        val builder = AlertDialog.Builder(this)
        with(builder) {
            setTitle("Logout?")
            setMessage("Are you sure you want to Logout?")
            setPositiveButton("Yes", DialogInterface.OnClickListener { _, _ ->
                Firebase.auth.signOut()

                GoogleSignIn.getClient(
                    context,
                    GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).build()
                ).signOut()

                val intent = Intent(context,SignInActivity::class.java)
                startActivity(intent)
                finish()
            })
            setNegativeButton("No", DialogInterface.OnClickListener { dialog, _ ->
                dialog.dismiss()
            })

            show()
        }
    }
}