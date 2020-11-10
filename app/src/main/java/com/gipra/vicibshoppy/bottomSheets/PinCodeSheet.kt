package com.gipra.vicibshoppy.bottomSheets

import android.content.Context
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.gipra.vicibshoppy.R
import com.gipra.vicibshoppy.utlis.showToast
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import kotlinx.android.synthetic.main.shoppy_picode_sheet.*

class PinCodeSheet : BottomSheetDialogFragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.shoppy_picode_sheet, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        pinCode.addTextChangedListener(object  : TextWatcher {
            override fun afterTextChanged(s: Editable?) {

            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
              if (s.toString().length < 6 || s.toString().length >6 )
                  pinCode.setError("PinCode length must be 6")
            }

        })

        submitPincode.setOnClickListener {
             var pincodeValue = pinCode.text
            savePincode(pincodeValue.toString())
        }


    }

    private fun savePincode(pincodeValue: String?) {
        val sp =
            activity!!.getSharedPreferences("User", Context.MODE_PRIVATE).edit()
        sp.clear()
        sp.putString("pinCode", pincodeValue)
        sp.commit()

        activity!!.showToast("PinCode added successfully")

        if (!sp.equals(null)){
          dismiss()
        }
    }
}