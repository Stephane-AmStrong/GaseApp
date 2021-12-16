package com.amstrong.gaseapp.ui.waiter

import android.content.ContentValues.TAG
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Rect
import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.amstrong.gaseapp.R
import com.amstrong.gaseapp.data.db.entities.relations.LineItemWithTaxes
import com.amstrong.gaseapp.data.network.Resource
import com.amstrong.gaseapp.data.network.MainApi
import com.amstrong.gaseapp.data.repositories.MainRepository
import com.amstrong.gaseapp.databinding.FragmentReceiptBinding
import com.amstrong.gaseapp.ui.MainViewModel
import com.amstrong.gaseapp.ui.base.BaseFragment
import com.amstrong.gaseapp.ui.dialogs.DiablogLineItem
import com.amstrong.gaseapp.ui.recycler_rows.LineItemWithTaxesRow
import com.amstrong.gaseapp.util.toast
import com.amstrong.gaseapp.utils.*
import com.amstrong.mvvmsampleproject.data.db.AppDatabase
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.OnItemClickListener
import com.xwray.groupie.ViewHolder
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import kotlin.collections.ArrayList
import kotlin.math.roundToInt

class ReceiptCreationFragment : BaseFragment<MainViewModel, FragmentReceiptBinding, MainRepository>() {

    private var forEdition: Boolean? = false
    private var canManageHall: Boolean = false
    private lateinit var groupLines: ArrayList<LineItemWithTaxes>
    private lateinit var lineItemsWithTaxes: List<LineItemWithTaxes>
    private lateinit var mAdapter: GroupAdapter<ViewHolder>


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        forEdition = arguments?.getBoolean(ARG_FOR_EDITION);
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        loadSpinner()


        val trashBinIcon = resources.getDrawable(
                R.drawable.ic_baseline_delete_forever_24,
                null
        )

        val myCallback = object : ItemTouchHelper.SimpleCallback(0,
                ItemTouchHelper.RIGHT) {
            override fun onMove(
                    recyclerView: RecyclerView,
                    viewHolder: RecyclerView.ViewHolder,
                    target: RecyclerView.ViewHolder
            ): Boolean = false

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                removeGroupedLines(viewHolder.adapterPosition)
            }

            override fun onChildDraw(
                    c: Canvas,
                    recyclerView: RecyclerView,
                    viewHolder: RecyclerView.ViewHolder,
                    dX: Float,
                    dY: Float,
                    actionState: Int,
                    isCurrentlyActive: Boolean
            ) {

                super.onChildDraw(c, recyclerView, viewHolder,
                        dX, dY, actionState, isCurrentlyActive)

                c.clipRect(0f, viewHolder.itemView.top.toFloat(),
                        dX, viewHolder.itemView.bottom.toFloat())

                if (dX < recyclerView.width / 3)
                    c.drawColor(Color.GRAY)
                else
                    c.drawColor(Color.RED)

                val textMargin = resources.getDimension(R.dimen.text_margin)
                        .roundToInt()

                trashBinIcon.bounds = Rect(
                        textMargin,
                        viewHolder.itemView.top + textMargin,
                        textMargin + trashBinIcon.intrinsicWidth,
                        viewHolder.itemView.top + trashBinIcon.intrinsicHeight
                                + textMargin
                )

                trashBinIcon.draw(c)
            }

        }


        val myHelper = ItemTouchHelper(myCallback)
        myHelper.attachToRecyclerView(binding.include.recyclerView)

        binding.rowTotal.getRoot().visible(false)

        binding.include.progressBar.visible(false)
        viewModel.lineItemsWithTaxes.observe(viewLifecycleOwner, Observer {
            binding.include.progressBar.visible(it is Resource.Loading)
            when (it) {
                is Resource.Success -> {
                    updateUI(it.value)
                }
            }
        })

        viewModel.lineItemWithTaxes.observe(viewLifecycleOwner, Observer {
            binding.include.progressBar.visible(it is Resource.Loading)

            when (it) {
                is Resource.Success -> {
                    parentFragment?.let { parent -> DiablogLineItem().show(parent.childFragmentManager, tag) }
                }
            }
        })

    }

    private fun updateUI(lineItemslineItemsWithTaxes: List<LineItemWithTaxes>) {
        this.lineItemsWithTaxes = lineItemslineItemsWithTaxes
        groupLines = arrayListOf()
        val distinctLine = lineItemslineItemsWithTaxes.distinctBy { it.lineItem.variant_id }

        for (line in distinctLine) {
            groupLines.add(line.apply { qte = (lineItemslineItemsWithTaxes.count { it.lineItem.variant_id == line.lineItem.variant_id }).toInt() })
        }

        with(binding) {
            if (lineItemslineItemsWithTaxes.any()) rowTotal.getRoot().visible(true) else rowTotal.getRoot().visible(false)
            rowTotal.txtName.text = "Total"
            rowTotal.txtQte.visible(false)
            rowTotal.txtAmount.text = lineItemslineItemsWithTaxes.sumByFloat { it.lineItem.price }.toDecimalFormat()

            mAdapter = GroupAdapter<ViewHolder>().apply {
                addAll(groupLines.toLineItemWithTaxesRow())
                setOnItemClickListener(onItemClickListener)
            }


//            include.recyclerView.addItemDecoration(DividerItemDecoration(context, LinearLayoutManager.VERTICAL))


            include.recyclerView.apply {
                layoutManager = LinearLayoutManager(context)
                setHasFixedSize(true)
                adapter = mAdapter

            }
        }
    }

    private fun loadSpinner() {
        val diningOptions = resources.getStringArray(R.array.dining_option);
        val dataAdapter = ArrayAdapter(requireContext(), R.layout.simple_spinner_item, diningOptions)
        dataAdapter.setDropDownViewResource(R.layout.simple_spinner_drop_down)

        binding.txtDiningOption.setAdapter(dataAdapter);
        binding.txtDiningOption.onItemSelectedListener = onItemSelectedListener
    }


    override fun getViewModel() = MainViewModel::class.java

    override fun getFragmentBinding(
            inflater: LayoutInflater,
            container: ViewGroup?
    ) = FragmentReceiptBinding.inflate(inflater, container, false)

    override fun getFragmentRepository(): MainRepository {
        val token = runBlocking { userPreferences.authToken.first() }
        canManageHall = runBlocking { userPreferences.canManageHall.first()!! }
        val apiMenu = remoteDataSource.buildApi(MainApi::class.java, token)
        val db = AppDatabase(requireContext())
        return MainRepository(apiMenu, db)
    }

    private val onItemClickListener = OnItemClickListener { row, view ->
        row as LineItemWithTaxesRow
        viewModel.selectLineItem(row.lineItemWithTaxes.toLineItem())
    }

    private val onItemSelectedListener = object : AdapterView.OnItemSelectedListener {

        override fun onNothingSelected(parent: AdapterView<*>?) {
            viewModel.selectDiningOption(resources.getStringArray(R.array.dining_option)[0])
        }

        override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
            viewModel.selectDiningOption(resources.getStringArray(R.array.dining_option)[position])
        }
    }

    private fun List<LineItemWithTaxes>.toLineItemWithTaxesRow(): List<LineItemWithTaxesRow> {
        return this.map {
            LineItemWithTaxesRow(it)
        }
    }


    private fun removeGroupedLines(position: Int) {
        val variantId: String = groupLines[position].lineItem.variant_id
        var linesTobeDeleted : List<LineItemWithTaxes> = arrayListOf()

        if(canManageHall){
            linesTobeDeleted = lineItemsWithTaxes.filter { it.lineItem.variant_id == variantId}
        }else{
            linesTobeDeleted = lineItemsWithTaxes.filter { it.lineItem.variant_id == variantId && it.lineItem.receipt_id == null }
        }


        if (linesTobeDeleted.any()) {
            for (line in linesTobeDeleted) {
                viewModel.removeLineItemsFromRoom(line.lineItem)
            }
            mAdapter.notifyItemRemoved(position)
        } else {
            toast(getString(R.string.error_dish_is_being_processed))
            mAdapter.notifyDataSetChanged()
        }
    }


    companion object {
        private const val ARG_FOR_EDITION = "arg_for_edition"

        @JvmStatic
        fun newInstance(forEdition: Boolean): AuMenuFragment {
            return AuMenuFragment().apply {
                arguments = Bundle().apply {
                    putBoolean(ARG_FOR_EDITION, forEdition)
                }
            }
        }
    }


}