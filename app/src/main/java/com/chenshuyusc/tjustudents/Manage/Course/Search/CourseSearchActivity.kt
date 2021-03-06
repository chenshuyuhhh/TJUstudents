package com.chenshuyusc.tjustudents.Manage.Course.Search

import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import cn.edu.twt.retrox.recyclerviewdsl.ItemAdapter
import cn.edu.twt.retrox.recyclerviewdsl.ItemManager
import cn.edu.twt.retrox.recyclerviewdsl.withItems
import com.chenshuyusc.tjustudents.Entity.Selection
import com.chenshuyusc.tjustudents.Entity.Course
import com.chenshuyusc.tjustudents.Entity.Student
import com.chenshuyusc.tjustudents.Manage.Course.Detail.CourseDetailActivity
import com.chenshuyusc.tjustudents.Manage.Course.Home.addCourse
import com.chenshuyusc.tjustudents.Manage.Course.score.ScoreActivity
import com.chenshuyusc.tjustudents.Manage.Selection.Detail.SelectionDetailActivity
import com.chenshuyusc.tjustudents.Manage.Selection.Home.addSelection
import com.chenshuyusc.tjustudents.Manage.Student.StudentDetail.StuDetailActivity
import com.chenshuyusc.tjustudents.Manage.Student.studentHome.addStudent
import com.chenshuyusc.tjustudents.Manage.Student.studentSearch.StuSearchPresenter
import com.chenshuyusc.tjustudents.R
import com.chenshuyusc.tjustudents.Util.ConstValue
import com.google.gson.Gson
import es.dmoral.toasty.Toasty

class CourseSearchActivity : AppCompatActivity(), CourseSearchView {
    private lateinit var back: ImageView
    private lateinit var input: EditText
    private lateinit var search: ImageView
    private lateinit var keyword: String

    private lateinit var itemManager: ItemManager

    private val presenter = CourseSearchPresenter(this)

    private lateinit var recyclerView: RecyclerView
    private lateinit var linearLayoutManager: LinearLayoutManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_course_search)
        bindID()
        linearLayoutManager = LinearLayoutManager(this)
        recyclerView.layoutManager = LinearLayoutManager(this)
        search.setOnClickListener {
            getKey()
            presenter.getSearch(keyword)
        }
        back.setOnClickListener {
            onBackPressed()
        }
    }

    fun bindID() {
        back = findViewById(R.id.course_search_iv_back)
        input = findViewById(R.id.course_search_et_input)
        search = findViewById(R.id.course_search_iv_icon)
        recyclerView = findViewById(R.id.course_search_rv)
    }

    // 获得搜索关键字
    fun getKey() {
        keyword = input.text.toString()
    }

    override fun onSuccess(course: Course, selections: List<Selection>) {
        recyclerView.withItems {
            addCourse(course) { judge, cid ->
                jump(judge, cid)
            }
            repeat(selections.size) { j ->
                addSelection(selections[j]) { judge, selection ->
                    jump(judge, selection)
                }
            }

        }
        itemManager = (recyclerView.adapter as ItemAdapter).itemManager as ItemManager
    }

    override fun onNull(status: String) {
        if (status == ConstValue.BLANK) {
            recyclerView.withItems { }
            Toasty.info(this, "哭了，小的没有搜到，嘤嘤嘤～ ", Toast.LENGTH_LONG, true).show()

        } else {
            Toasty.info(this, "哭了，这个课没有人选，呜呜呜～ ", Toast.LENGTH_LONG, true).show()
        }
    }

    override fun onError(msg: String) {
        Toasty.error(this, msg, Toast.LENGTH_LONG, true).show()
    }

    private fun jump(judge: String, selection: Selection) {
        val intent = Intent()
        val bundle = Bundle()
        if (judge == ConstValue.UPDATE) {
            bundle.putString(ConstValue.KEY_STATUS, ConstValue.UPDATE)
            bundle.putString(ConstValue.SELECTION, Gson().toJson(selection))
            intent.putExtras(bundle)
            intent.setClass(this@CourseSearchActivity, SelectionDetailActivity::class.java)
            startActivity(intent)
        } else if (judge == ConstValue.DELETE) {
            presenter.deleteSelection(Gson().toJson(selection), keyword)
        }
    }

    private fun jump(judge: String, cid: String) {
        val intent = Intent()
        val bundle = Bundle()
        when (judge) {
            ConstValue.UPDATE -> {
                bundle.putString(ConstValue.KEY_STATUS, ConstValue.UPDATE)
                bundle.putString(ConstValue.CID, cid)
                intent.putExtras(bundle)
                intent.setClass(this@CourseSearchActivity, CourseDetailActivity::class.java)
                startActivity(intent)
            }
            ConstValue.DELETE -> presenter.deleteSelection(cid, keyword)
            ConstValue.SCORE -> {
                bundle.putString(ConstValue.CID, cid)
                intent.putExtras(bundle)
                intent.setClass(this@CourseSearchActivity, ScoreActivity::class.java)
                startActivity(intent)
            }
        }
    }
}