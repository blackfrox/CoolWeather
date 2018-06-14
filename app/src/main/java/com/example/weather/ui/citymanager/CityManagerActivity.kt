package com.example.weather.ui.citymanager

import android.app.Activity
import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.graphics.Canvas
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.helper.ItemTouchHelper
import android.text.TextUtils
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import com.chad.library.adapter.base.callback.ItemDragAndSwipeCallback
import com.chad.library.adapter.base.listener.OnItemDragListener
import com.chad.library.adapter.base.listener.OnItemSwipeListener
import com.example.weather.R
import com.example.weather.base.BaseActivity
import com.example.weather.mvp.contract.CityManagerContract
import com.example.weather.mvp.presenter.CityManagerPresenter
import com.example.weather.other.db.CityWeather
import com.example.weather.ui.choose.ChooseActivity
import com.example.weather.ui.adapter.CityManagerAdapter
import com.example.weather.util.tool.RxBus
import com.example.weather.util.event.MainEvent
import com.example.weather.util.initToolbar
import kotlinx.android.synthetic.main.activity_city_manager.*
import kotlinx.android.synthetic.main.toolbar.*
import org.litepal.crud.DataSupport

/**
 * 问题: 数据从哪里获取?
 * 答: 笨，当然是从数据库里获取咯！
 */
class CityManagerActivity : BaseActivity(), CityManagerContract.View {

    override fun getLayoutId(): Int {
        return R.layout.activity_city_manager
    }


    override lateinit var presenter: CityManagerContract.Presenter
    private lateinit var mAdapter: CityManagerAdapter
    private lateinit var list: List<CityWeather>
    private var dataChanged = false
    override fun initView(savedInstanceState: Bundle?) {
        initToolbar(toolbar)
        presenter = CityManagerPresenter(this)

        list = DataSupport.findAll(CityWeather::class.java)
        mAdapter = CityManagerAdapter(list)
        recyclerView.apply {
            layoutManager = LinearLayoutManager(this@CityManagerActivity)
            adapter = mAdapter
        }
        mAdapter.apply {
            //            val itemDragAndSwipeCallback = ItemDragAndSwipeCallback(mAdapter)
            val itemDragAndSwipeCallback = object : ItemDragAndSwipeCallback(mAdapter) {
                override fun onMove(recyclerView: RecyclerView?, source: RecyclerView.ViewHolder?, target: RecyclerView.ViewHolder?): Boolean =
                        if (source?.adapterPosition == 0 || target?.adapterPosition == 0)
                            false
                        else
                            super.onMove(recyclerView, source, target)

                override fun getMovementFlags(recyclerView: RecyclerView?, viewHolder: RecyclerView.ViewHolder?): Int {
                    if (viewHolder?.adapterPosition == 0)
                        return makeMovementFlags(0, 0)
                    else
                        return super.getMovementFlags(recyclerView, viewHolder)
                }

            }
            val itemTouchHelper = ItemTouchHelper(itemDragAndSwipeCallback)
            itemTouchHelper.attachToRecyclerView(recyclerView)
            itemDragAndSwipeCallback.setSwipeMoveFlags(ItemTouchHelper.START or ItemTouchHelper.END)


            var draStartPosition = 0
            // 开启拖拽
            enableDragItem(itemTouchHelper, R.id.root, true)
            setOnItemDragListener(object : OnItemDragListener {
                override fun onItemDragMoving(source: RecyclerView.ViewHolder?, from: Int, target: RecyclerView.ViewHolder?, to: Int) {
                }

                override fun onItemDragStart(viewHolder: RecyclerView.ViewHolder?, pos: Int) {
                    draStartPosition = pos
                }

                override fun onItemDragEnd(viewHolder: RecyclerView.ViewHolder?, pos: Int) {
                    if (draStartPosition != pos) {
                        dataChanged=true
                    }
                }

            })

            //思路: 删除的时候，1 数据库的更新，2 mAdapter的更新  3 dataChanged =true (用于通知MainActivity更新)
            // 开启滑动
            mAdapter.enableSwipeItem()
            setOnItemSwipeListener(object : OnItemSwipeListener {
                //1 删除数据库中的数据 2 弹出提示  3 更新adapter
                override fun onItemSwiped(viewHolder: RecyclerView.ViewHolder?, pos: Int) {
                    val deleteItem = mAdapter.data[pos]
                    Snackbar.make(recyclerView, "删除成功！", Snackbar.LENGTH_LONG)
                            .apply {
                                //数据库保存失败
//                                setAction("撤销"){
////                                    deleteItem.save() //重新保存
//                                    CityWeather(deleteItem.countyName,pos)
//                                            .save()
//                                    mAdapter.addData(pos,deleteItem)
//                                    mAdapter.notifyItemInserted(pos)
//                                    dataChanged=false
//                                }
                            }
                            .show()
                    //原来数据库的删除操作需要放在mAdapter之前，不然会报错IndexOutException
                    DataSupport.deleteAll(CityWeather::class.java, "id = ?", mAdapter.data[pos].id.toString())
                    mAdapter.apply {
                        data.removeAt(pos)
                        notifyItemRemoved(pos)
                    }
                    dataChanged=true
                }
                override fun onItemSwipeStart(viewHolder: RecyclerView.ViewHolder?, pos: Int) {

                }
                override fun clearView(viewHolder: RecyclerView.ViewHolder?, pos: Int) {

                }
                override fun onItemSwipeMoving(canvas: Canvas?, viewHolder: RecyclerView.ViewHolder?, dX: Float, dY: Float, isCurrentlyActive: Boolean) {

                }
            })

        }
    }

    /**
     * 1 数据库添加数据 2 adapter添加item
     * 因为添加操作只有一次，所以不需要updateAll
     */
    private var isAdded= true
    override fun addData(countyName: String) {
        val item = CityWeather(countyName,mAdapter.data.size).apply { save() }
        mAdapter.apply {
            addData(item)
            notifyDataSetChanged()
        }
        val list=DataSupport.findAll(CityWeather::class.java)
        Log.d("CityManagerActivity","addData() adapter size ::::::${mAdapter.data.size}")
        Log.d("CityManagerActivity","addData() list size ::::::${list.size}")
    }

    /**
     * 原本是打算将添加、删除、交换通过三个变量进行分开处理，但是考虑到用户可能进行多次操作,会造成无效，
     * 所以还是用dataChanged一个变量就够了
     */
    override fun onBackPressed() {
        when{
            isAdded -> RxBus.instance.post(MainEvent())
            dataChanged ->{
                //更新数据库中的天气数据
//            val first=DataSupport.findFirst(CityWeather::class.java)
//            DataSupport.deleteAll(CityWeather::class.java)

                //todo:bug 数据库不能保存数据  1
                //删除或添加操作后，数据库中的数据的顺序不是我们想要的,所以需要更换id
                // 全部删除之后添加  2 更新

                //问题，更新数据库表中的数据id时，报错： UNIQUE constraint failed: cityweather.id (code 1555)
//            for ((i,item) in mAdapter.data.withIndex()){
//                val values=ContentValues().apply {
//                    put("id",i)
//                }
//                DataSupport.updateAll(CityWeather::class.java,values,"countyName = ?",item.countyName)
//            }

                DataSupport.deleteAll(CityWeather::class.java)
                for (item in mAdapter.data)
                    item.save()
                val list=DataSupport.findAll(CityWeather::class.java)
                Log.d("CityManagerActivity","dataChange adapter size ::::::${mAdapter.data.size}")
                Log.d("CityManagerActivity","dataChange  list size ::::::${list.size}")
                mAdapter
                list
                RxBus.instance.post(MainEvent())
            }
        }
        super.onBackPressed()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_city_manager, menu)
        return true
    }

    private val ADD_ITEM = 1
    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when (item?.itemId) {
            R.id.menu_add -> {
                val intent = Intent(this@CityManagerActivity, ChooseActivity::class.java)
                startActivityForResult(intent, ADD_ITEM)
            }
            else -> return super.onOptionsItemSelected(item)
        }
        return true
    }

    //todo: 一开始以为是RxBus导致的闪退，所以使用startActivityForResult代替，后续可以修改
    //mAdapter的更新需要放在当前Activity/Fragment的处于用户可操作状态才能进行,否则会崩溃(crash)
    //解决办法是放在onBackPress()方法中去通知上一个Activity处理更新操作
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        when (resultCode) {
            Activity.RESULT_OK -> {
                val countyName = data?.getStringExtra("item")
                if (!TextUtils.isEmpty(countyName)){
                    addData(countyName!!)
                }
            }

        }
    }

    companion object {

        fun start(context: Context) {
            val intent = Intent(context, CityManagerActivity::class.java)
            context.startActivity(intent)
        }
    }
}
