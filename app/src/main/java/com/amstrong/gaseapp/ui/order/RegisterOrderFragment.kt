package com.amstrong.gaseapp.ui.order

import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Rect
import android.os.Bundle
import android.view.*
import androidx.core.util.Pair
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.amstrong.gaseapp.R
import com.amstrong.gaseapp.data.db.entities.Order
import com.amstrong.gaseapp.data.db.entities.OrderLine
import com.amstrong.gaseapp.data.db.entities.Store
import com.amstrong.gaseapp.data.db.entities.Supplier
import com.amstrong.gaseapp.data.network.MainApi
import com.amstrong.gaseapp.data.network.Resource
import com.amstrong.gaseapp.data.repositories.MainRepository
import com.amstrong.gaseapp.databinding.FragmentRegisterOrderBinding
import com.amstrong.gaseapp.ui.MainActivity
import com.amstrong.gaseapp.ui.MainViewModel
import com.amstrong.gaseapp.ui.adapters.StoreAdapter
import com.amstrong.gaseapp.ui.adapters.SupplierAdapter
import com.amstrong.gaseapp.ui.base.BaseFragment
import com.amstrong.gaseapp.ui.dialogs.DialogAddOrderLine
import com.amstrong.gaseapp.ui.recycler_rows.OrderLineRow
import com.amstrong.gaseapp.util.toast
import com.amstrong.gaseapp.utils.*
import com.amstrong.mvvmsampleproject.data.db.AppDatabase

import com.google.android.material.datepicker.MaterialDatePicker
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.OnItemClickListener
import com.xwray.groupie.ViewHolder
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle
import kotlin.math.roundToInt

class RegisterOrderFragment : BaseFragment<MainViewModel, FragmentRegisterOrderBinding, MainRepository>() {

    companion object {
        fun newInstance() = RegisterOrderFragment()
    }

    private var _store: Store? = null
    private var _supplier: Supplier? = null
    private var _order: Order? = null

    private var dateFormatter = DateTimeFormatter.ofLocalizedDate(FormatStyle.LONG)
    private val materialDateBuilder = MaterialDatePicker.Builder.dateRangePicker()
    private lateinit var dateRangePicker: MaterialDatePicker<Pair<Long, Long>>

    private var orderedAt : Long = MaterialDatePicker.thisMonthInUtcMilliseconds()
    private var expectedAt : Long = MaterialDatePicker.todayInUtcMilliseconds()

    private lateinit var mAdapter: GroupAdapter<ViewHolder>

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        materialDateBuilder.setSelection(
            Pair(
                orderedAt,
                expectedAt
            )
        ).setTitleText(resources.getString(R.string.selectionner_intervalle_date))

        dateRangePicker = materialDateBuilder.build()

        binding.crdDateInterval.setOnClickListener(onClickListener)

        dateRangePicker.addOnPositiveButtonClickListener {
            try {
                binding.txtDateInterval.text = (resources.getString(R.string.pattern_interval_of_time, it.first.toLocalDateTime().format(dateFormatter), it.second.toLocalDateTime().format(dateFormatter)))
                orderedAt =  it.first
                expectedAt =  it.second
            }catch (ex : Exception){
                ex.message?.let { it1 -> toast(it1) }
            }
        }


        binding.fab.setOnClickListener(onClickListener)

        binding.txtSupplier.setOnItemClickListener() { adapterView, view, position, id ->
            _supplier = adapterView.adapter.getItem(position) as Supplier?
            binding.txtSupplier.setText(_supplier?.name)
        }


        binding.txtStore.setOnItemClickListener() { adapterView, view, position, id ->
            _store = adapterView.adapter.getItem(position) as Store?
            binding.txtStore.setText(_store?.name)
        }
        ////////////

        val trashBinIcon = resources.getDrawable(
            R.drawable.ic_baseline_delete_forever_24,
            null
        )

        val myCallback = object: ItemTouchHelper.SimpleCallback(0,ItemTouchHelper.RIGHT) {
            override fun onMove(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                target: RecyclerView.ViewHolder
            ): Boolean =false

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                //removeGroupedLines(viewHolder.adapterPosition)
                _order?.order_lines?.removeAt(viewHolder.adapterPosition)
                mAdapter.notifyItemRemoved(viewHolder.adapterPosition)
                updateUIOrder(_order)
//                viewModel.getLineItemsFromSqLite()
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

                if(dX < recyclerView.width / 3)
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
        myHelper.attachToRecyclerView(binding.recyclerView)

        ///////////
        viewModel.selectedOrder.observe(viewLifecycleOwner, Observer {
            binding.progressBar.visible(it is Resource.Loading)

            when (it) {
                is Resource.Success -> {
                    binding.progressBar.visible(false)
                    updateUIOrder(it.value)
                }

                is Resource.Loading -> {
                    binding.progressBar.visible(true)
                }

                is Resource.Failure -> handleApiError(it) {

                }
            }
        })


        viewModel.registeredOrder.observe(viewLifecycleOwner, Observer {
            binding.progressBar.visible(it is Resource.Loading)

            when (it) {
                is Resource.Success -> {
                    binding.progressBar.visible(false)

                    toast(resources.getString(R.string.order_recorded))

                    viewModel.releaseRegisteredOrder()
                    viewModel.setOrder(null)
                    val action = RegisterOrderFragmentDirections.navigateToOrdersFragment()
                    findNavController().navigate(action)
                }
                is Resource.Loading -> {
                    binding.progressBar.visible(true)
                }
                is Resource.Failure -> handleApiError(it) {

                }
            }
        })


        viewModel.suppliers.observe(viewLifecycleOwner, Observer {
            binding.progressBar.visible(it is Resource.Loading)

            when (it) {
                is Resource.Success -> {
                    binding.progressBar.visible(false)
                    updateUISupplier(it.value)
                }
                is Resource.Loading -> {
                    binding.progressBar.visible(true)
                }
                is Resource.Failure -> handleApiError(it) {

                }
            }
        })

        viewModel.stores.observe(viewLifecycleOwner, Observer {
            binding.progressBar.visible(it is Resource.Loading)

            when (it) {
                is Resource.Success -> {
                    binding.progressBar.visible(false)
                    updateUIStore(it.value)
                }
                is Resource.Loading -> {
                    binding.progressBar.visible(true)
                }
                is Resource.Failure -> handleApiError(it) {

                }
            }
        })

        //dates
        /*
        binding.txtOrderOn.setText(Date().formatToPattern("d MMM yyyy"))

        val materialDateBuilder: MaterialDatePicker.Builder<*> =
            MaterialDatePicker.Builder.datePicker()
        materialDateBuilder.setTitleText(resources.getString(R.string.selectionner_date))

        val datePickerOrderOn = materialDateBuilder.build()
        val datePickerExpectedOn = materialDateBuilder.build()

        binding.txtOrderOn.setOnTouchListener(OnTouchListener { v, event ->
            if (event.action == MotionEvent.ACTION_UP) {
                datePickerOrderOn.show(childFragmentManager, "MATERIAL_DATE_PICKER")
                return@OnTouchListener true
            }
            false
        })


        binding.txtExpectedOn.setOnTouchListener(OnTouchListener { v, event ->
            if (event.action == MotionEvent.ACTION_UP) {
                datePickerExpectedOn.show(childFragmentManager, "MATERIAL_DATE_PICKER")
                return@OnTouchListener true
            }
            false
        })


        datePickerOrderOn.addOnPositiveButtonClickListener {
            try {
                binding.txtOrderOn.setText(datePickerOrderOn.headerText)
            }catch (ex: Exception){

            }
        }

        datePickerExpectedOn.addOnPositiveButtonClickListener {
            try {
                binding.txtExpectedOn.setText(datePickerExpectedOn.headerText)
            }catch (ex: Exception){

            }
        }
        */
    }

    private fun updateUIOrder(order: Order?) {
        this._order = order

        if (_order!=null){
            with(binding){

                if (order?.id!=null){
                    _supplier = order.supplier
                    _store = order.store

                    txtSupplier.setText(order.supplier?.name)
                    txtStore.setText(order.store?.name)
                    txtReference.setText(order.reference)

                    try {
                        orderedAt =  order.purchase_date.time
                        expectedAt =  order.expected_date?.time ?: MaterialDatePicker.todayInUtcMilliseconds()

                        txtDateInterval.text = (resources.getString(R.string.pattern_interval_of_time, orderedAt.toLocalDateTime().format(dateFormatter), expectedAt.toLocalDateTime().format(dateFormatter)))
                    }catch (ex : Exception){
                        ex.message?.let { it1 -> toast(it1) }
                    }

                    txtNote.setText(order.note)


                }

                mAdapter = GroupAdapter<ViewHolder>().apply {

                    addAll(order?.order_lines!!.toOrderLineRow())
                    setOnItemClickListener(onItemClickListener)
                }

                recyclerView.apply {
                    layoutManager = LinearLayoutManager(context)
                    setHasFixedSize(true)
                    adapter = mAdapter
                }
            }
        }
    }


    private fun updateUISupplier(suppliers: List<Supplier>) {

        val lstSuppliers = arrayListOf<Supplier>()
        lstSuppliers.addAll(suppliers)

        val adapter = SupplierAdapter(requireContext(), R.layout.item_auto_complete_text_view, suppliers)
        binding.txtSupplier.setAdapter(adapter)
        binding.txtSupplier.threshold = 2

    }

    private fun updateUIStore(stores: List<Store>) {
        val adapter = StoreAdapter(requireContext(), R.layout.item_auto_complete_text_view, stores)
        binding.txtStore.setAdapter(adapter)
        binding.txtStore.threshold = 1
    }


//    private fun ready() : Boolean {
//        return !(binding.txtName.text.toString().isBlank() && binding.txtEmail.text.toString().isBlank())
//    }


    override fun getViewModel() = MainViewModel::class.java

    override fun getFragmentBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ) = FragmentRegisterOrderBinding.inflate(inflater, container, false)


    override fun getFragmentRepository(): MainRepository {
        val token = runBlocking { userPreferences.authToken.first() }
        val api = remoteDataSource.buildApi(MainApi::class.java, token)
        val db = AppDatabase(requireContext())
        return MainRepository(api, db)
    }


    private val onClickListener = View.OnClickListener {
        val mainActivity = requireActivity()
        mainActivity as MainActivity

        when(it.id){

            R.id.crd_date_interval -> {
                dateRangePicker.show(childFragmentManager, "MATERIAL_DATE_PICKER")
                true
            }

            R.id.fab -> {
                if((_order?.status != requireContext().getString(R.string.order_status_closed))){
                    parentFragment?.let { parent -> DialogAddOrderLine().show(parent.childFragmentManager, tag) }
                }else{
                    toast(requireContext().getString(R.string.error_order_closed))
                }
                true
            }
            else -> false
        }
    }

    private fun List<OrderLine>.toOrderLineRow() : List<OrderLineRow>{
        return this.map {
            OrderLineRow(it)
        }
    }

    private val onItemClickListener = OnItemClickListener{ row, view ->
        row as OrderLineRow
        viewModel.setOrderLine(row.orderLine)
        parentFragment?.let { parent -> DialogAddOrderLine().show(parent.childFragmentManager, tag) }
//        order?.order_lines?.remove(row.orderLine)
//        updateUIOrder(order)
    }

    private fun isOk() : Boolean{
        with(binding){
            if (txtReference.text.toString().isEmpty()){
                txtReference.error = resources.getString(R.string.error_field_required)
            }

            if (txtSupplier.text.toString().isEmpty()){
                txtSupplier.error = resources.getString(R.string.error_field_required)
            }

            if (txtStore.text.toString().isEmpty()){
                txtStore.error = resources.getString(R.string.error_field_required)
            }

            if (txtDateInterval.text.toString() == resources.getString(R.string.selectionner_intervalle_date)){
                toast(resources.getString(R.string.selectionner_intervalle_date))
            }

            return !(txtReference.text.toString().isEmpty() || txtSupplier.text.toString().isEmpty()  || txtStore.text.toString().isEmpty() || txtDateInterval.text.toString() == resources.getString(R.string.selectionner_intervalle_date))

        }

    }


    private fun saveOrder() {

        if (isOk() && _order != null && _supplier != null && _store != null) {
            _order?.apply {
                supplier_id = _supplier?.id
                store_id = _store?.id
                reference = binding.txtReference.text.toString()
                employee_id = null

                purchase_date = orderedAt.toLocalDateTime().toDate()

                expected_date = expectedAt.toLocalDateTime().toDate()
                note = binding.txtNote.text.toString()

                status = resources.getString(R.string.order_status_pending)
            }

            viewModel.registerOrder(_order!!)
        }
    }


    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        if(_order?.status != requireContext().getString(R.string.order_status_closed)) inflater.inflate(R.menu.make_order, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_place_order -> {
                if( _order==null && _order!!.status != requireContext().getString(R.string.order_status_closed)){
                    toast(requireContext().getString(R.string.error_order_closed))
                }else{
                    saveOrder()
                }

                if (_order!=null){
                    toast(_order!!.status)
                }

                true
            }

            R.id.action_save_as_draft -> {

                true
            }

            R.id.action_delete_order -> {
                if (_order!=null && _order!!.id != null){
                    viewModel.deleteOrder(_order!!)

                    val action = RegisterOrderFragmentDirections.navigateToOrdersFragment()
                    findNavController().navigate(action)
                }
                true
            }

            R.id.action_cancel -> {

                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }


}




























