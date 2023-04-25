//package com.example.customviews
//
//import android.content.Context
//import android.content.res.ColorStateList
//import android.content.res.TypedArray
//import android.graphics.Color
//import android.os.Parcel
//import android.os.Parcelable
//import android.util.AttributeSet
//import android.view.LayoutInflater
//import androidx.constraintlayout.widget.ConstraintLayout
//import com.example.customviews.databinding.MyButtonsBinding
//
//enum class ButtonsAction{
//    POSITIVE, NEGATIVE
//}
//
//typealias ButtonsActionListener = (ButtonsAction)->Unit
//
//class MyButtonsView(
//    context: Context,
//    attrs: AttributeSet?,
//    defStyleAttrs: Int,
//    defStyleRes: Int
//) : ConstraintLayout(context, attrs, defStyleAttrs, defStyleRes) {
//    private val binding: MyButtonsBinding
//
//    constructor(context: Context, attrs: AttributeSet?, defStyleAttrs: Int) : this(context, attrs, defStyleAttrs, 0)
//    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, 0)
//    constructor(context: Context) : this(context, null)
//
//    init {
//        val inflater = LayoutInflater.from(context)
//        inflater.inflate(R.layout.my_buttons, this, true)
//        binding  = MyButtonsBinding.bind(this)
//        initViewByAttr(attrs, defStyleAttrs, defStyleRes)
//    }
//
//    private fun initViewByAttr(attrs: AttributeSet?, defStyleAttrs: Int, defStyleRes: Int){
//        if (attrs==null) return
//        val typedArray = context.obtainStyledAttributes(attrs, R.styleable.MyButtonsView, defStyleAttrs, defStyleRes)
//        parseAttr(typedArray)
//        typedArray.recycle()
//    }
//
//    private fun parseAttr(typedArray: TypedArray) = with(binding) {
//        val positiveButtonText = typedArray.getString(R.styleable.MyButtonsView_positiveButtonText)
//        val positiveButtonColor = typedArray.getColor(R.styleable.MyButtonsView_positiveButtonColor, Color.BLACK)
//        val negativeButtonText = typedArray.getString(R.styleable.MyButtonsView_negativeButtonText)
//        val negativeButtonColor = typedArray.getColor(R.styleable.MyButtonsView_negativeButtonColor, Color.BLACK)
//
//        positiveButton.text = positiveButtonText
//        positiveButton.backgroundTintList = ColorStateList.valueOf(positiveButtonColor)
//        negativeButton.text = negativeButtonText
//        negativeButton.backgroundTintList = ColorStateList.valueOf(negativeButtonColor)
//
//        val progressMode = typedArray.getBoolean(R.styleable.MyButtonsView_progressMode, false)
//        initProgressBar(progressMode)
//    }
//
//    private fun initProgressBar(progressMode:Boolean) = with(binding){
//        if (progressMode) {
//            progressBar.visibility = VISIBLE
//            positiveButton.visibility = INVISIBLE
//            negativeButton.visibility = INVISIBLE
//        } else {
//            progressBar.visibility = GONE
//            positiveButton.visibility = VISIBLE
//            negativeButton.visibility = VISIBLE
//        }
//    }
//
//    fun setOnPositiveButtonClickListener(listener: OnClickListener){
//        binding.positiveButton.setOnClickListener(listener)
//    }
//
//    override fun onSaveInstanceState(): Parcelable? {
//        val superState = super.onSaveInstanceState()!!
//        val ourSavedState = OurSavedState(superState)
//        ourSavedState.positiveButtonText = binding.positiveButton.text.toString()
//        return ourSavedState
//    }
//
//    override fun onRestoreInstanceState(state: Parcelable?) {
//        val ourState = state as OurSavedState
//        super.onRestoreInstanceState(ourState.superState)
//        binding.positiveButton.text = ourState.positiveButtonText
//    }
//
//    class OurSavedState : BaseSavedState {
//        var positiveButtonText: String? = null
//
//        constructor(superState: Parcelable) : super(superState)
//
//        constructor(parcel: Parcel) : super(parcel) {
//            positiveButtonText = parcel.readString()
//        }
//
//        override fun writeToParcel(out: Parcel, flags: Int) {
//            super.writeToParcel(out, flags)
//            out.writeString(positiveButtonText)
//        }
//
//        companion object {
//            @JvmField
//            val CREATOR = object : Parcelable.Creator<OurSavedState> {
//                override fun createFromParcel(source: Parcel): OurSavedState {
//                    return OurSavedState(source)
//                }
//
//                override fun newArray(size: Int): Array<OurSavedState?> {
//                    return Array(size) { null }
//                }
//            }
//        }
//    }
//}