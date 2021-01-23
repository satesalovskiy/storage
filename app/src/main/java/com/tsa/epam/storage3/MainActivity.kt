package com.tsa.epam.storage3

import android.content.Context
import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Environment
import androidx.core.app.ActivityCompat
import com.tsa.epam.storage3.databinding.ActivityMainBinding
import java.io.File
import kotlin.concurrent.thread


class MainActivity : AppCompatActivity() {
    companion object {
        private const val TEXT_KEY = "TEXT_KEY"
        private const val INTERNAL_FILE_NAME = "internal_storage.txt"
        private const val EXTERNAL_FILE_NAME = "external.txt"
    }

    private val binding: ActivityMainBinding by lazy {
        val tmp = ActivityMainBinding.inflate(layoutInflater)
        setContentView(tmp.root)
        tmp
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding.saveToPrefs.setOnClickListener {
            saveToPrefs(binding.inputText.editableText.toString())
        }
        binding.saveToInternal.setOnClickListener {
            saveToInternal(binding.inputText.editableText.toString())
        }
        binding.saveToExternal.setOnClickListener {
            saveToExternal(binding.inputText.editableText.toString())
        }
        binding.saveToDb.setOnClickListener {
            saveToDB(binding.inputText.editableText.toString())
        }
        binding.loadFromPrefs.setOnClickListener {
            loadFromPrefs()?.let {
                binding.outputText.text = it
            }
        }
        binding.loadFromInternal.setOnClickListener {
            loadFromInternal()?.let {
                binding.outputText.text = it
            }
        }

        binding.loadFromExternal.setOnClickListener {
            binding.outputText.text = loadFromExternal()
        }
        binding.loadFromDb.setOnClickListener {
            loadFromDB()
        }

    }

    private fun saveToPrefs(text: String) {
        val prefs = getPreferences(MODE_PRIVATE)

        prefs.edit().apply {
            putString(TEXT_KEY, text)
            apply()
        }
    }

    private fun saveToInternal(text: String) {
        val file = File(filesDir, INTERNAL_FILE_NAME)

        try {
            val output = file.outputStream()
            output.write(text.toByteArray())
            output.close()
        } catch (ex: Exception) {
            ex.printStackTrace()
        }
    }

    private fun saveToExternal(text: String) {
        checkPermissions()
        if (isExternalStorageWritable()) {
            val file = File(getExternalFilesDir(null), EXTERNAL_FILE_NAME)
            try {
                val output = file.outputStream()
                output.write(text.toByteArray())
                output.close()
            } catch (ex: Exception) {
                ex.printStackTrace()
            }
        }
    }

    // Checks if a volume containing external storage is available
// for read and write.
    fun isExternalStorageWritable(): Boolean {
        return Environment.getExternalStorageState() == Environment.MEDIA_MOUNTED
    }

    // Checks if a volume containing external storage is available to at least read.
    fun isExternalStorageReadable(): Boolean {
        return Environment.getExternalStorageState() in
                setOf(Environment.MEDIA_MOUNTED, Environment.MEDIA_MOUNTED_READ_ONLY)
    }

    private fun hasPermissions(context: Context, vararg permissions: String): Boolean =
            permissions.all {
                ActivityCompat.checkSelfPermission(context, it) == PackageManager.PERMISSION_GRANTED
            }

    private fun checkPermissions() {
        if (!hasPermissions(
                        this,
                        android.Manifest.permission.WRITE_EXTERNAL_STORAGE,
                        android.Manifest.permission.READ_EXTERNAL_STORAGE
                )
        ) {
            ActivityCompat.requestPermissions(
                    this,
                    arrayOf(
                            android.Manifest.permission.WRITE_EXTERNAL_STORAGE,
                            android.Manifest.permission.READ_EXTERNAL_STORAGE
                    ),
                    321
            )
        }
    }

    private fun saveToDB(text: String) {
        val db = DBHelper.getDBInstance(this)
        thread {
            db.getItemsDao().insertItem(Item(text = text))
        }
    }

    private fun loadFromPrefs(): String? {
        val prefs = getPreferences(MODE_PRIVATE)
        prefs.getString(TEXT_KEY, getString(R.string.no_value_saved))?.let {
            return it
        } ?: return null
    }

    private fun loadFromInternal(): String? {
        val file = File(filesDir, INTERNAL_FILE_NAME)

        var read: String? = null

        try {
            val input = file.inputStream()
            read = input.readBytes().decodeToString()
            input.close()

        } catch (ex: Exception) {
            ex.printStackTrace()
        }


        return read ?: getString(R.string.no_value_saved)
    }

    private fun loadFromExternal(): String {
        checkPermissions()

        var read: String? = null
        if (isExternalStorageReadable()) {
            val file = File(getExternalFilesDir(null), EXTERNAL_FILE_NAME)
            try {
                val input = file.inputStream()
                read = input.readBytes().decodeToString()
                input.close()

            } catch (ex: Exception) {
                ex.printStackTrace()
            }
        }

        return read ?: getString(R.string.no_value_saved)
    }

    private fun loadFromDB() {
        val db = DBHelper.getDBInstance(this)
        thread {
            val data = db.getItemsDao().getAllItems().toString()
            runOnUiThread {
                binding.outputText.text = data
            }
        }
    }
}