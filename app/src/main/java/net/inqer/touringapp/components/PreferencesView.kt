package net.inqer.touringapp.components

import android.content.Context
import android.content.SharedPreferences
import android.content.res.XmlResourceParser
import android.util.AttributeSet
import android.util.Log
import android.util.Xml
import android.view.LayoutInflater
import android.widget.LinearLayout
import com.google.android.material.textfield.TextInputLayout
import com.google.android.material.textview.MaterialTextView
import dagger.hilt.android.AndroidEntryPoint
import net.inqer.touringapp.R
import org.xmlpull.v1.XmlPullParser
import org.xmlpull.v1.XmlPullParserException
import java.io.IOException
import javax.inject.Inject


@AndroidEntryPoint
class PreferencesView @JvmOverloads constructor(
        context: Context,
//        sharedPreferences: SharedPreferences? = null,
        attrs: AttributeSet? = null,
//        defStyle: Int = 0,
//        defStyleRes: Int = 0
) : LinearLayout(context, attrs) {

    @Inject
    lateinit var sharedPreferences: SharedPreferences;

    private val parser: XmlResourceParser
    private val layoutInflater: LayoutInflater
    private val textFields: HashMap<String, TextInputLayout> = HashMap()

    init {
        orientation = VERTICAL

        layoutInflater = LayoutInflater.from(context)

        parser = context.resources.getXml(R.xml.settings_main)

        parseAndInflate();
    }

    private fun parseAndInflate() {
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
                        appendCategoryHeader(Xml.asAttributeSet(parser).getAttributeValue(xmlns, "title"))
                    }
                    "Preference" -> {
                        val attrs = Xml.asAttributeSet(parser)
                        val key = attrs.getAttributeValue(xmlns, "key")
                        appendTextField(
                                key,
                                attrs.getAttributeValue(xmlns, "title"),
                                sharedPreferences.getString(key, "")
                        )
                    }
                }
            }
        } while (state != XmlPullParser.END_DOCUMENT)
    }

    private fun appendCategoryHeader(title: String? = "Параметры") {
        Log.d(TAG, "appendCategoryHeader: $title")
        val view = layoutInflater.inflate(R.layout.item_category_header, this, true)
        view.findViewById<MaterialTextView>(R.id.header_title)?.text = title
    }

    private fun appendTextField(key: String, title: String?, value: String?) {
        Log.d(TAG, "appendTextField: $title")
        val view = layoutInflater.inflate(R.layout.item_setting, this, true)
        val textInputLayout = view.findViewById<TextInputLayout>(R.id.text_field).apply {
            hint = title
            editText?.setText(value)
        }
        textFields[key] = textInputLayout
    }

    fun save() {
        val editor = sharedPreferences.edit()
        textFields.forEach {
            Log.d(TAG, "save: ${it.key} ${it.value.editText?.text}")
            editor.putString(it.key, it.value.editText?.text.toString())
        }

        editor.apply()
    }

    companion object {
        private const val TAG = "PreferencesView"
        private const val xmlns = "http://schemas.android.com/apk/res-auto"
    }
}