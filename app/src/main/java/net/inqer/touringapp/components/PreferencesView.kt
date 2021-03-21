package net.inqer.touringapp.components

import android.content.Context
import android.content.res.XmlResourceParser
import android.util.AttributeSet
import android.util.Log
import android.util.Xml
import android.view.LayoutInflater
import android.widget.LinearLayout
import com.google.android.material.textfield.TextInputLayout
import com.google.android.material.textview.MaterialTextView
import net.inqer.touringapp.R
import org.xmlpull.v1.XmlPullParser
import org.xmlpull.v1.XmlPullParserException
import java.io.IOException


class PreferencesView @JvmOverloads constructor(
        context: Context,
        attrs: AttributeSet? = null,
//        defStyle: Int = 0,
//        defStyleRes: Int = 0
) : LinearLayout(context, attrs) {

    private val parser: XmlResourceParser
    private val layoutInflater: LayoutInflater

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
                        appendTextField(Xml.asAttributeSet(parser).getAttributeValue(xmlns, "title"))
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

    private fun appendTextField(title: String?) {
        Log.d(TAG, "appendTextField: $title")
        val view = layoutInflater.inflate(R.layout.item_setting, this, true)
        view.findViewById<TextInputLayout>(R.id.text_field)?.hint = title
    }

    companion object {
        private const val TAG = "PreferencesView"
        private const val xmlns = "http://schemas.android.com/apk/res-auto"
    }
}