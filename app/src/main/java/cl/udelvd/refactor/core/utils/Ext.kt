package cl.udelvd.refactor.core.utils

import cl.udelvd.refactor.core.domain.model.Domain

fun <T : Domain> processFilterIds(filterList: List<T>): String {
    var ids = ""

    for (i in filterList.indices) {

        when {
            i != filterList.size - 1 -> ids += "${filterList[i].id};"
            i == filterList.size - 1 -> ids += "${filterList[i].id}"
        }
    }
    return ids;
}