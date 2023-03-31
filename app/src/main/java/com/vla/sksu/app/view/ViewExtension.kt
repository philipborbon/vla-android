package com.vla.sksu.app.view

import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout



val TextInputEditText.inputLayout: TextInputLayout?
    get() = parent.parent as? TextInputLayout