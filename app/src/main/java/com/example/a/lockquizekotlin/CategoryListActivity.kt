package com.example.a.lockquizekotlin

import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.provider.BaseColumns
import android.util.Log
import android.view.ViewGroup
import android.widget.Button
import android.widget.LinearLayout
import com.example.a.lockquizekotlin.DBContract.CategoryContract
import com.example.a.lockquizekotlin.DBContract.QuestionContract
import kotlinx.android.synthetic.main.activity_categorylist.*
import kotlinx.android.synthetic.main.activity_categorylist.view.*

class CategoryListActivity : AppCompatActivity() {
    private val TAG = "CategoryListActivity"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_categorylist)

        val isOx = intent.extras.getBoolean("ox")
        // category_entry 테이블을 읽어와서
        val categoryTable = readAllCategoryTable()

        // 각 카테고리 만큼 버튼을 제작하고 (버튼은 각자 자기 id 를 가지고 있어야 한다) (categoy_list에 추가해야 한다.)
        for (entry in categoryTable) {
            // 각 버튼은 오답노트용 / 그냥 문제 풀이용 이벤트로 갈림
            // 문제 activity에 자기 버튼 id와 문제 풀이용인지, 오답노트 용인지 체크 한 것을 보낸다.
            val button = Button(applicationContext)
            button.text = entry.category
            button.id = entry.id
            button.layoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT, 1f)
            button.background.alpha = 0
            button.setTextColor(Color.WHITE) // TODO 텍스트 컬러를 동적으로 바꿔주고 있긴한데.... 어캐할까?
            category_list.addView(button)

            button.setOnClickListener {
                if (isOx) { // 오답 노트용 QuestionActivity를 만들어야 한다.
                    val intent = Intent(applicationContext, QuestionActivity::class.java)
                    intent.putExtra("ox", true)
                    intent.putExtra("category_id", entry.id)
                    startActivity(intent)
                }
                else {
                    val intent = Intent(applicationContext, QuestionActivity::class.java)
                    intent.putExtra("ox", false)
                    intent.putExtra("category_id", entry.id)
                    startActivity(intent)
                }
            }
        }


    }

    private fun readAllCategoryTable(): MutableList<CategoryContract.Entry>{
        val dbHelper = CategoryContract.DbHelper(applicationContext)
        val db = dbHelper.readableDatabase

        // 데이터베이스 컬럼 중에서 알아낼 prjection을 정의한다.
        val projection = arrayOf(CategoryContract.Schema.COLUMN_ID, CategoryContract.Schema.COLUMN_NAME)

        val cursor = db?.query(
                CategoryContract.Schema.TABLE_NAME,
                projection,
                null,
                null,
                null,
                null,
                null
        )

        val items = mutableListOf<CategoryContract.Entry>()
        cursor?.let {
            with(cursor) {
                while (moveToNext()) {
                    val id = getInt(getColumnIndexOrThrow(CategoryContract.Schema.COLUMN_ID))
                    val category = getString(getColumnIndexOrThrow(CategoryContract.Schema.COLUMN_NAME))
                    val entry = CategoryContract.Entry(id, category)
                    items.add(entry)
                }
            }
        }

        Log.d(TAG, "####read category db items : ")
        for (item in items){
            Log.d(TAG, "db item : $item")
        }
        Log.d(TAG, "###################")

        return items
    }
}
