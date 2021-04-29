package net.inqer.touringapp.components

import android.content.Context
import android.content.SharedPreferences
import android.content.res.XmlResourceParser
import android.text.InputType
import android.util.AttributeSet
import android.util.Log
import android.util.Xml
import android.view.LayoutInflater
import android.view.inputmethod.EditorInfo
import android.widget.LinearLayout
import androidx.core.view.children
import com.google.android.material.textfield.TextInputLayout
import com.google.android.material.textview.MaterialTextView
import dagger.hilt.android.AndroidEntryPoint
import net.inqer.touringapp.R
import net.inqer.touringapp.preferences.AppConfig
import org.xmlpull.v1.XmlPullParser
import org.xmlpull.v1.XmlPullParserException
import java.io.IOException
import javax.inject.Inject


@AndroidEntryPoint
class PreferencesView @JvmOverloads constructor(
        context: Context,
        attrs: AttributeSet? = null,
//        defStyle: Int = 0,
//        defStyleRes: Int = 0
) : LinearLayout(context, attrs) {

    @Inject
    lateinit var sharedPreferences: SharedPreferences;

    @Inject
    lateinit var config: AppConfig

    private var parser: XmlResourceParser? = null
    private val layoutInflater: LayoutInflater
    private val fieldsToSave: HashMap<String, TextInputLayout> = HashMap()

    init {
        orientation = VERTICAL

        layoutInflater = LayoutInflater.from(context)

        context.theme.obtainStyledAttributes(
                attrs,
                R.styleable.PreferencesView,
                0, 0).apply {

            try {
                val preferences = this.getResourceId(R.styleable.PreferencesView_preferences, 0)
                if (preferences == 0) {
                    Log.e(TAG, "init: resource id not found, aborting!")
                    return@apply
                }

                parser = context.resources.getXml(preferences)

                parser?.let { parseAndInflate(it) }

            } catch (reason: Throwable) {
                Log.e(TAG, "init: failed to obtain settings res", reason)
            } finally {
                recycle()
            }
        }
    }

    private fun parseAndInflate(parser: XmlResourceParser) {
        var state = 0
        do {
            try {
                state = parser.next()
            } catch (e1: XmlPullParserException) {
                e1.printStackTrace()
            } catch (e1: IOException) {
                e1.printStackTrace()
            }
            if (state == XmlPullParser.START_TAG) {
                Log.d(TAG, "Parser name: ${parser.name}")

                when (parser.name) {
                    "PreferenceCategory" -> {
                        appendCategoryHeader(Xml.asAttributeSet(parser).getAttributeValue(xmlns, ATTR_TITLE))
                    }
                    "Preference" -> {
                        val attrs = Xml.asAttributeSet(parser)
                        val keyRes: Int = attrs.getAttributeValue(xmlns, ATTR_KEY).substring(1).toInt()
                        val type = attrs.getAttributeIntValue(xmlns, ATTR_TYPE, EditorInfo.TYPE_NULL)
                        val hint: String? = attrs.getAttributeValue(xmlns, ATTR_HINT)
                        val title: String? = attrs.getAttributeValue(xmlns, ATTR_TITLE)
                        val key = context.getString(keyRes)

                        when (keyRes) {
                            R.string.key_main_server_address ->
                                appendField(key, title, config.baseUrl, type, hint)

                            R.string.key_location_poll_interval ->
                                appendField(key, title, config.locationPollInterval.toString(), type, hint)

                            R.string.key_location_waypoint_radius ->
                                appendField(key, title, config.waypointEnterRadius.toString(), type, hint)

                            else ->
                                Log.e(TAG, "parseAndInflate: unknown preference type! - $type")
                        }
                    }
                }
            }
        } while (state != XmlPullParser.END_DOCUMENT)
    }


    private fun appendCategoryHeader(title: String? = "Параметры") {
        val view = layoutInflater.inflate(R.layout.item_category_header, this, false) as LinearLayout
        val textView = view.children.find { child ->
            Log.d(TAG, "appendCategoryHeader: child = $child")
            child is MaterialTextView
        } as MaterialTextView?
        textView?.text = title

        this.addView(view)
    }


    private fun appendField(key: String, title: String?, value: String?, fieldType: Int, helperText: String? = null) {
        Log.d(TAG, "appendTextField: $key $title $value")
        val textInputLayout = layoutInflater.inflate(R.layout.item_setting, this, false) as TextInputLayout
        textInputLayout.apply {
            this.hint = title
            this.helperText = helperText
            this.editText?.apply {
                inputType = fieldType
                setText(value)
            }
        }
        fieldsToSave[key] = textInputLayout

        this.addView(textInputLayout)
    }


    fun save() {
        val editor = sharedPreferences.edit()
        fieldsToSave.forEach { entry ->
            Log.d(TAG, "save: ${entry.key} ${entry.value.editText?.text}")

            entry.value.editText?.let { editText ->
                when (editText.inputType) {
                    InputType.TYPE_CLASS_TEXT -> {
                        editor.putString(entry.key, entry.value.editText?.text.toString())
                    }

                    InputType.TYPE_CLASS_NUMBER -> {
                        try {
                            val value = editText.text.toString().toInt()
                            editor.putInt(entry.key, value)
                        } catch (exception: NumberFormatException) {
                            Log.e(TAG, "save: ${entry.key} value is not a valid number",
                                    exception)
                        }
                    }

                    else -> {
                        Log.e(TAG, "save: can't save, unknown input type! - ${editText.inputType}")
                    }
                }
            }

        }

        editor.apply()
    }


    companion object {
        private const val TAG = "PreferencesView"
        private const val xmlns = "http://schemas.android.com/apk/res/android"

        private const val ATTR_KEY = "key"
        private const val ATTR_TITLE = "title"
        private const val ATTR_HINT = "hint"
        private const val ATTR_TYPE = "inputType"
        private const val TYPE_NUMBER = "number"
        private const val TYPE_DECIMAL = "numberDecimal"
    }
}