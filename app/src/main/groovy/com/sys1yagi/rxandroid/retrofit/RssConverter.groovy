package com.sys1yagi.rxandroid.retrofit

import com.sys1yagi.rxandroid.models.Item
import groovy.transform.CompileStatic
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import org.jsoup.nodes.Element
import org.jsoup.parser.Parser
import retrofit.converter.ConversionException
import retrofit.converter.Converter
import retrofit.mime.TypedInput
import retrofit.mime.TypedOutput

import java.lang.reflect.Type

@CompileStatic
public class RssConverter implements Converter {

    @Override
    Object fromBody(TypedInput body, Type type) throws ConversionException {
        def items = []
        Document document = Jsoup.parse(body.in(), "utf-8", "", Parser.xmlParser())

        document.select("item").collect({ Element child ->
            Item item = new Item()
            item.title = child.select("title").first().text()
            item.url = child.select("link").first().text()
            items.add(item)
        })
        return items
    }

    @Override
    TypedOutput toBody(Object object) {
        return null
    }
}
