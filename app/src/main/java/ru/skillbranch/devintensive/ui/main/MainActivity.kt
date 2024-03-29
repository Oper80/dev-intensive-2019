package ru.skillbranch.devintensive.ui.main

import android.content.Intent
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.InsetDrawable
import android.os.Bundle
import android.view.Menu
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.appcompat.widget.SearchView
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.chip.Chip
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.activity_main.*
import ru.skillbranch.devintensive.App
import ru.skillbranch.devintensive.R
import ru.skillbranch.devintensive.extensions.dp
import ru.skillbranch.devintensive.models.data.UserItem
import ru.skillbranch.devintensive.ui.adapters.ChatAdapter
import ru.skillbranch.devintensive.ui.adapters.ChatItemTouchHelperCallback
import ru.skillbranch.devintensive.ui.group.GroupActivity
import ru.skillbranch.devintensive.viewmodels.MainViewModel

class MainActivity : AppCompatActivity() {

    private lateinit var chatAdapter: ChatAdapter
    private lateinit var viewModel: MainViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        setTheme(R.style.AppTheme)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        initToolbar()
        initViews()
        initViewModel()
    }

    private fun initToolbar() {
        setSupportActionBar(toolbar)
        supportActionBar?.setBackgroundDrawable(ColorDrawable(ContextCompat.getColor(App.applicationContext(),
                if(AppCompatDelegate.getDefaultNightMode() == AppCompatDelegate.MODE_NIGHT_YES) R.color.color_toolbar_night
                else R.color.color_primary)))
    }

    private fun initViews() {
        chatAdapter = ChatAdapter {
            Snackbar.make(rv_chat_list, "Click on ${it.title}", Snackbar.LENGTH_LONG).show()
        }
        val myDivider = InsetDrawable(resources.getDrawable(R.drawable.chat_divider, theme), 72.dp, 0, 0, 0)
        val divider = DividerItemDecoration(this, DividerItemDecoration.VERTICAL)
        divider.setDrawable(myDivider)

        val touchCallback = ChatItemTouchHelperCallback(chatAdapter) {chatItem ->
            viewModel.addToArchive(chatItem.id)
            Snackbar.make(rv_chat_list, "Вы точно хотите добавить ${chatItem.title} в архив?", Snackbar.LENGTH_LONG)
                    .setAction("UNDO") {
                        viewModel.restoreFromArchive(chatItem.id)
                    }.show()
        }
        val touchHelper = ItemTouchHelper(touchCallback)
        touchHelper.attachToRecyclerView(rv_chat_list)

        with(rv_chat_list) {
            adapter = chatAdapter
            layoutManager = LinearLayoutManager(this@MainActivity)
            addItemDecoration(divider)
        }

        fab.setOnClickListener {
            val intent = Intent(this, GroupActivity::class.java)
            startActivity(intent)
        }
    }

    private fun initViewModel() {
        viewModel = ViewModelProviders.of(this).get(MainViewModel::class.java)
        viewModel.getChatData().observe(this, Observer { chatAdapter.updateData(it) })
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_search, menu)
        val searchItem = menu?.findItem(R.id.action_search)
        val searchView = searchItem?.actionView as SearchView
        searchView.queryHint = "Введите имя пользователя"
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String): Boolean {
                viewModel.handleSearchQuery(query)
                return true
            }

            override fun onQueryTextChange(query: String): Boolean {
                viewModel.handleSearchQuery(query)
                return true
            }

        })
        return super.onCreateOptionsMenu(menu)
    }
}
