/*
* Copyright 2016 Ville Tainio
*
* Licensed under the Apache License, Version 2.0 (the "License");
* you may not use this file except in compliance with the License.
* You may obtain a copy of the License at
*
*    http://www.apache.org/licenses/LICENSE-2.0
*
* Unless required by applicable law or agreed to in writing, software
* distributed under the License is distributed on an "AS IS" BASIS,
* WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
* See the License for the specific language governing permissions and
* limitations under the License.
*/

package com.villetainio.familiarstrangers.util

import android.content.res.AssetManager
import java.io.BufferedReader
import java.io.InputStreamReader
import java.util.*

class NameGenerator  {

    fun generateName(gender: String, assetManager: AssetManager) : String {
        when (gender) {
            "male" -> return generateMaleName(assetManager)
            "female" -> return generateFemaleName(assetManager)
        }

        return ""
    }

    private fun generateMaleName(assetManager: AssetManager) : String {
        val names = readLinesFromFile("male.txt", assetManager)
        val index = Random().nextInt(names.size)
        return names.get(index)
    }

    private fun generateFemaleName(assetManager: AssetManager) : String {
        val names = readLinesFromFile("female.txt", assetManager)
        val index = Random().nextInt(names.size)
        return names.get(index)
    }

    private fun readLinesFromFile(filename: String, assetManager: AssetManager) : ArrayList<String> {
        val namesList = ArrayList<String>()

        val inputStream = assetManager.open(filename)
        val reader = BufferedReader(InputStreamReader(inputStream))

        var line = reader.readLine()
        while (line != null) {
            namesList.add(line)
            line = reader.readLine()
        }

        return namesList
    }
}
