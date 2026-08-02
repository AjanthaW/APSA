package com.ajantha.apsa.util

import android.content.Context
import com.ajantha.apsa.model.InstalledApp
import com.google.gson.GsonBuilder
import java.io.File

fun String.safeCsv(): String {
    return "\"" + this.replace(
        "\"",
        "\"\""
    ) + "\""
}

fun List<String>.toSafeString(): String {
    return joinToString(";")
}

fun exportFullCsv(
    context: Context,
    apps: List<InstalledApp>
) {

    val file = File(
        context.getExternalFilesDir(null),
        "apps_full.csv"
    )

    file.bufferedWriter()
        .use { writer ->

            writer.write(
                "package,app_name,apk,target_sdk,min_sdk," + "debuggable,backup,system,cleartext,installer," + "perm_count,danger_perm_count," + "exp_act_count,exp_serv_count,exp_recv_count,exp_prov_count," + "permissions,dangerous_permissions," + "activities,services,receivers,providers\n"
            )

            apps.forEach {
                writer.write(it.toFullCsvRow() + "\n")
            }
        }
}

fun exportMlDataset(
    context: Context,
    apps: List<InstalledApp>
) {

    val file = File(
        context.getExternalFilesDir(null),
        "dataset.csv"
    )

    file.bufferedWriter()
        .use { writer ->

            writer.write(
                "perm_count,danger_perm,exp_act,exp_serv,exp_recv,exp_prov," + "debuggable,backup,system,cleartext," + "target_sdk,min_sdk," + "sms,location,contacts,mic,camera,label\n"
            )

            apps.forEach { app ->
                writer.write("${app.toFeatureRow()},${app.getLabel()}\n")
            }
        }
}

fun exportFullJson(
    context: Context,
    apps: List<InstalledApp>
) {

    val file = File(
        context.getExternalFilesDir(null),
        "apps_full.json"
    )

    val gson = GsonBuilder().setPrettyPrinting()
        .create()

    file.writeText(gson.toJson(apps))
}

fun exportAll(
    context: Context,
    apps: List<InstalledApp>
) {

    exportFullJson(
        context,
        apps
    )
    exportFullCsv(
        context,
        apps
    )
    exportMlDataset(
        context,
        apps
    )
}