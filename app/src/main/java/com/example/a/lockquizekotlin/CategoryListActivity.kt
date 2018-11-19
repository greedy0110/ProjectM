package com.example.a.lockquizekotlin

import android.content.Intent
import android.graphics.Color
import android.os.Build
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.util.TypedValue
import android.widget.Button
import android.widget.ImageView
import android.widget.LinearLayout
import com.example.a.lockquizekotlin.DBContract.CategoryDB
import com.example.a.lockquizekotlin.Utils.LayoutUtils
import kotlinx.android.synthetic.main.activity_categorylist.*

class CategoryListActivity : GreedyActivity() {
    private val TAG = "CategoryListActivity"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_categorylist)

        val isOx = intent.extras.getBoolean("ox")
        // category_entry 테이블을 읽어와서
        val categoryTable = CategoryDB.readAll(applicationContext)

        if (!isOx) { // 오답노트 일 때, 오답노트 전체보기 버튼을 추가시켜 주어야한다.
            val button = createListButton()
            button.text = "전체 보기"
            button.id = 0
            category_list.addView(button)

            addButtonBorder()

            button.setOnClickListener {
                val intent = Intent(applicationContext, QuestionActivity::class.java)
                intent.putExtra("ox", false)
                intent.putExtra("category_id", -1) // -1 일때는 전체보기로 가즈아
                startActivity(intent)
            }
        }

        // 각 카테고리 만큼 버튼을 제작하고 (버튼은 각자 자기 id 를 가지고 있어야 한다) (categoy_list에 추가해야 한다.)
        for (i in 0..categoryTable.size - 1) {
            val entry = categoryTable[i]
            // 각 버튼은 오답노트용 / 그냥 문제 풀이용 이벤트로 갈림
            // 문제 activity에 자기 버튼 id와 문제 풀이용인지, 오답노트 용인지 체크 한 것을 보낸다.
            val button = createListButton()
            button.text = entry.category
            button.id = entry.id
            category_list.addView(button)

            if (i != categoryTable.size - 1) { // 마지막 줄은 생성안함
                addButtonBorder()
            }

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

        acl_end_button.setOnClickListener {
            LayoutUtils.goBack(this)
        }
    }

    fun createListButton(): Button {
        val button = Button(applicationContext)
        button.layoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT, 1f)
        button.background.alpha = 0
        if (Build.VERSION.SDK_INT >= 23)
            button.setTextAppearance(R.style.ButtonStyle)
        button.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 14f) // 14dp 로 설정
        return button
    }

    fun addButtonBorder(){
        val factor = applicationContext.resources.displayMetrics.density
        val border = ImageView(applicationContext)
        border.setBackgroundColor(Color.GRAY)
        border.layoutParams = LinearLayout.LayoutParams((100 * factor).toInt(), LinearLayout.LayoutParams.WRAP_CONTENT, 0.02f)
        category_list.addView(border)
    }
}
