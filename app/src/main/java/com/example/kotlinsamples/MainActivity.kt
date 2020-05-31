package com.example.kotlinsamples

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.widget.TextView
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.example.kotlinsamples.databinding.ActivityMainBinding
import kotlinx.coroutines.*
import org.apache.commons.compress.archivers.zip.ZipFile
import java.io.BufferedInputStream
import java.io.BufferedOutputStream
import java.io.File
import java.io.FileOutputStream
import java.nio.channels.Channels


class MainActivity : FragmentActivity() {

    //声明全局变量
    companion object {
        const val REQUEST_CODE_PERMISSION = 0x01
    }

    private lateinit var path: String
    private lateinit var desPath: String
    private lateinit var tvLoad: TextView

    private lateinit var mMainViewModel: MainViewModel
    private lateinit var viewBinding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        viewBinding =  ActivityMainBinding.inflate(layoutInflater)
        setContentView(viewBinding.root)

        viewBinding.tvMainLoad.text = "Main ViewBing TextView"
        //setContentView(R.layout.activity_main)
        mMainViewModel = ViewModelProvider(this)[MainViewModel::class.java]
        // SD卡路径 /storage/sdcard0
        path = Environment.getExternalStorageDirectory().absolutePath

        desPath = filesDir.parentFile?.absolutePath.toString()

        requestPermission()
    }

    private fun select() {
        GlobalScope.launch(Dispatchers.Main) {
            viewBinding.tvMainLoad.text = "正在加载数据包..."
            val timeStart = System.currentTimeMillis()
            println("coroutine Thread 1 ${Thread.currentThread().name}")
            getData()
            val timeEnd = System.currentTimeMillis()
            val time = (timeEnd - timeStart) / 1000
            viewBinding.tvMainLoad.text = getString(R.string.str_load, time)
            launch {
                delay(2000)
            }
            startActivity(Intent(this@MainActivity, DataActivity::class.java))
        }
    }

    private suspend fun getData() {
        withContext(Dispatchers.IO) {
            Thread.sleep(1000)
            delay(1000)
            println("Coroutines Thread 2 ${Thread.currentThread().name}")

            println(path)
            val fileNames: MutableList<String> = mutableListOf()
            val fileTree: FileTreeWalk = File(path).walk()
            fileTree.maxDepth(1)
                .filter { it.isFile }
                .filter { it.name.startsWith("bdsoft_") }
                .filter { it.extension in listOf("zip") }
                .forEach { fileNames.add(it.absolutePath) }

            fileNames.forEach(::println)
            //解压文件
            try {

                if (File(desPath).exists()) {
                    val zf = ZipFile(fileNames[0], "GBK")
                    /*
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                        //ZipFile()
                        ZipFile(fileNames[0], "GBK")

                        //java.util.zip.ZipFile(fileNames[1], Charsets.ISO_8859_1)
                    } else {
                        ZipFile(fileNames[0], "GBK")
                    }
                    */
                    val enumeration = zf.entries
                    //val enumeration = zf.entries()
                    while (enumeration.hasMoreElements()) {
                        val zipEntry = enumeration.nextElement()
                        var zipEntryName = zipEntry.name
                        if (zipEntryName.contains("..")) {
                            continue
                        }
                        zipEntryName = zipEntryName.replace("\\", File.separator)
                        val zFile = File(desPath + File.separator + zipEntryName)
                        if (zipEntry.isDirectory) {
                            println("正在创建解压目录 —— $zipEntryName")
                            if (!zFile.exists()) {
                                zFile.mkdirs()
                            }
                        } else {
                            val parentFile = zFile.parentFile
                            parentFile?.let {
                                if (!it.exists()) {
                                    it.mkdirs()
                                }
                            }
                            println("正在创建解压文件 —— $zipEntryName")
                            /*
                            val bos = BufferedOutputStream(FileOutputStream(zFile))
                            val bis = BufferedInputStream(zf.getInputStream(zipEntry))

                            val buffer = ByteArray(1024)
                            var len: Int
                            var total = 0
                            while (((bis.read(buffer)).also { len = it }) != -1) {
                                bos.write(buffer, 0, len)
                                total += len
                                //获取当前下载量
                            }
                            bos.close()
                            bis.close()
                            */
                            try {
                                val fileChannelInput = Channels.newChannel(zf.getInputStream(zipEntry))
                                val fileChannelOutput = FileOutputStream(zFile).channel
                                fileChannelOutput.transferFrom(fileChannelInput, 0, Long.MAX_VALUE)
                            } catch (e: Exception) {
                                print(e.message)
                            }
                        }
                    }
                    zf.close()
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    private fun requestPermission () {

        val permissions = arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE)
        val checkSelfPermission = ContextCompat.checkSelfPermission(this,
            Manifest.permission.READ_EXTERNAL_STORAGE)
        if (checkSelfPermission == PackageManager.PERMISSION_GRANTED) {
            select()
        } else if (checkSelfPermission == PackageManager.PERMISSION_DENIED) {
            //不具备该权限，需要向用户申请该权限
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                ActivityCompat.requestPermissions(this, permissions, REQUEST_CODE_PERMISSION)
            } else {
                //如果用户此前没有拒绝过，首次调用时，则无需提示，直接请求权限。当用户此前拒绝过并且勾选下次不提示，也会返回false。
                ActivityCompat.requestPermissions(this, permissions, REQUEST_CODE_PERMISSION)
            }
            //第一次请求权限，                      ActivityCompat.shouldShowRequestPermissionRationale=false;
            //允许某权限后(去掉)                    ActivityCompat.shouldShowRequestPermissionRationale=false;
            //禁止权限，并选中【禁止后不再询问】       ActivityCompat.shouldShowRequestPermissionRationale=false；
            //第一次请求权限被禁止，但未选择【不再提醒】 ActivityCompat.shouldShowRequestPermissionRationale=true;
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        if (requestCode == REQUEST_CODE_PERMISSION) {

            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                //第0个权限请求成功
                Toast.makeText(this, "权限请求成功", Toast.LENGTH_SHORT).show()
                select()
            } else {
                //权限被手动拒绝
                Toast.makeText(this, "权限请求失败，应用需要储存卡写入权限，请在设置界面打开", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
