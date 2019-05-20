package com.magicalrice.project.library_base.base

import android.Manifest.permission.CALL_PHONE
import android.content.ComponentName
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import androidx.annotation.RequiresPermission
import androidx.core.content.FileProvider
import java.io.File
import java.util.*

/**
 * @package com.magicalrice.project.library_base.base
 * @author Adolph
 * @date 2019-04-22 Mon
 * @description 意图相关
 *
 * isIntentAvailable                : 判断意图是否可用
 * getInstallAppIntent              : 获取安装 App（支持 6.0）的意图
 * getUninstallAppIntent            : 获取卸载 App 的意图
 * getLaunchAppIntent               : 获取打开 App 的意图
 * getLaunchAppDetailsSettingsIntent: 获取 App 具体设置的意图
 * getShareTextIntent               : 获取分享文本的意图
 * getShareImageIntent              : 获取分享图片的意图
 * getComponentIntent               : 获取其他应用组件的意图
 * getShutdownIntent                : 获取关机的意图
 * getCaptureIntent                 : 获取拍照的意图
 */

object IntentUtils {
    /**
     * 判断意图是否可用
     *
     * @param intent The intent.
     * @return `true`: yes<br></br>`false`: no
     */
    fun isIntentAvailable(intent: Intent): Boolean {
        return AppManager.getInstance().getApp()
            .packageManager
            .queryIntentActivities(intent, PackageManager.MATCH_DEFAULT_ONLY)
            .size > 0
    }

    /**
     * 获取安装 App（支持 6.0）的意图
     *
     * Target APIs greater than 25 must hold
     * `<uses-permission android:name="android.permission.REQUEST_INSTALL_PACKAGES" />`
     *
     * @param filePath The path of file.
     * @return the intent of install app
     */
    fun getInstallAppIntent(filePath: String): Intent? {
        return getInstallAppIntent(getFileByPath(filePath), false)
    }

    /**
     * 获取安装 App（支持 6.0）的意图
     *
     * Target APIs greater than 25 must hold
     * `<uses-permission android:name="android.permission.REQUEST_INSTALL_PACKAGES" />`
     *
     * @param file The file.
     * @return the intent of install app
     */
    fun getInstallAppIntent(file: File): Intent? {
        return getInstallAppIntent(file, false)
    }

    /**
     * 获取安装 App（支持 6.0）的意图
     *
     * Target APIs greater than 25 must hold
     * `<uses-permission android:name="android.permission.REQUEST_INSTALL_PACKAGES" />`
     *
     * @param filePath  The path of file.
     * @param isNewTask True to add flag of new task, false otherwise.
     * @return the intent of install app
     */
    fun getInstallAppIntent(filePath: String, isNewTask: Boolean): Intent? {
        return getInstallAppIntent(getFileByPath(filePath), isNewTask)
    }

    /**
     * 获取安装 App（支持 6.0）的意图
     *
     * Target APIs greater than 25 must hold
     * `<uses-permission android:name="android.permission.REQUEST_INSTALL_PACKAGES" />`
     *
     * @param file      The file.
     * @param isNewTask True to add flag of new task, false otherwise.
     * @return the intent of install app
     */
    fun getInstallAppIntent(file: File?, isNewTask: Boolean): Intent? {
        if (file == null) return null
        val intent = Intent(Intent.ACTION_VIEW)
        val data: Uri
        val type = "application/vnd.android.package-archive"
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.N) {
            data = Uri.fromFile(file)
        } else {
            intent.flags = Intent.FLAG_GRANT_READ_URI_PERMISSION
            val authority =
                AppManager.getInstance().getApp().getPackageName() + ".utilcode.provider"
            data = FileProvider.getUriForFile(AppManager.getInstance().getApp(), authority, file)
        }
        intent.setDataAndType(data, type)
        return getIntent(intent, isNewTask)
    }

    /**
     * 获取卸载 App 的意图
     *
     * @param packageName The name of the package.
     * @return the intent of uninstall app
     */
    fun getUninstallAppIntent(packageName: String): Intent {
        return getUninstallAppIntent(packageName, false)
    }

    /**
     * 获取卸载 App 的意图
     *
     * @param packageName The name of the package.
     * @param isNewTask   True to add flag of new task, false otherwise.
     * @return the intent of uninstall app
     */
    fun getUninstallAppIntent(packageName: String, isNewTask: Boolean): Intent {
        val intent = Intent(Intent.ACTION_DELETE)
        intent.data = Uri.parse("package:$packageName")
        return getIntent(intent, isNewTask)
    }

    /**
     * 获取打开 App 的意图
     *
     * @param packageName The name of the package.
     * @return the intent of launch app
     */
    fun getLaunchAppIntent(packageName: String): Intent? {
        return getLaunchAppIntent(packageName, false)
    }

    /**
     * 获取打开 App 的意图
     *
     * @param packageName The name of the package.
     * @param isNewTask   True to add flag of new task, false otherwise.
     * @return the intent of launch app
     */
    fun getLaunchAppIntent(packageName: String, isNewTask: Boolean): Intent? {
        val intent =
            AppManager.getInstance().getApp().getPackageManager().getLaunchIntentForPackage(
                packageName
            )
                ?: return null
        return getIntent(intent, isNewTask)
    }

    /**
     * 获取 App 具体设置的意图
     *
     * @param packageName The name of the package.
     * @return the intent of launch app details settings
     */
    fun getLaunchAppDetailsSettingsIntent(packageName: String): Intent {
        return getLaunchAppDetailsSettingsIntent(packageName, false)
    }

    /**
     * 获取 App 具体设置的意图
     *
     * @param packageName The name of the package.
     * @param isNewTask   True to add flag of new task, false otherwise.
     * @return the intent of launch app details settings
     */
    fun getLaunchAppDetailsSettingsIntent(
        packageName: String,
        isNewTask: Boolean
    ): Intent {
        val intent = Intent("android.settings.APPLICATION_DETAILS_SETTINGS")
        intent.data = Uri.parse("package:$packageName")
        return getIntent(intent, isNewTask)
    }

    /**
     * 获取分享文本的意图
     *
     * @param content The content.
     * @return the intent of share text
     */
    fun getShareTextIntent(content: String): Intent {
        return getShareTextIntent(content, false)
    }

    /**
     * 获取分享文本的意图
     *
     * @param content   The content.
     * @param isNewTask True to add flag of new task, false otherwise.
     * @return the intent of share text
     */

    fun getShareTextIntent(content: String, isNewTask: Boolean): Intent {
        val intent = Intent(Intent.ACTION_SEND)
        intent.type = "text/plain"
        intent.putExtra(Intent.EXTRA_TEXT, content)
        return getIntent(intent, isNewTask)
    }

    /**
     * 获取分享图片的意图
     *
     * @param content   The content.
     * @param imagePath The path of image.
     * @return the intent of share image
     */
    fun getShareImageIntent(content: String, imagePath: String): Intent? {
        return getShareImageIntent(content, imagePath, false)
    }

    /**
     * 获取分享图片的意图
     *
     * @param content   The content.
     * @param imagePath The path of image.
     * @param isNewTask True to add flag of new task, false otherwise.
     * @return the intent of share image
     */
    fun getShareImageIntent(
        content: String,
        imagePath: String?,
        isNewTask: Boolean
    ): Intent? {
        return if (imagePath == null || imagePath.length == 0) null else getShareImageIntent(
            content,
            File(imagePath), isNewTask
        )
    }

    /**
     * 获取分享图片的意图
     *
     * @param content The content.
     * @param image   The file of image.
     * @return the intent of share image
     */
    fun getShareImageIntent(content: String, image: File): Intent? {
        return getShareImageIntent(content, image, false)
    }

    /**
     * 获取分享图片的意图
     *
     * @param content   The content.
     * @param image     The file of image.
     * @param isNewTask True to add flag of new task, false otherwise.
     * @return the intent of share image
     */
    fun getShareImageIntent(
        content: String,
        image: File?,
        isNewTask: Boolean
    ): Intent? {
        return if (image == null || !image.isFile) null else getShareImageIntent(
            content,
            file2Uri(image),
            isNewTask
        )
    }

    /**
     * 获取分享图片的意图
     *
     * @param content The content.
     * @param uri     The uri of image.
     * @return the intent of share image
     */
    fun getShareImageIntent(content: String, uri: Uri): Intent {
        return getShareImageIntent(content, uri, false)
    }

    /**
     * 获取分享图片的意图
     *
     * @param content   The content.
     * @param uri       The uri of image.
     * @param isNewTask True to add flag of new task, false otherwise.
     * @return the intent of share image
     */
    fun getShareImageIntent(
        content: String,
        uri: Uri?,
        isNewTask: Boolean
    ): Intent {
        val intent = Intent(Intent.ACTION_SEND)
        intent.putExtra(Intent.EXTRA_TEXT, content)
        intent.putExtra(Intent.EXTRA_STREAM, uri)
        intent.type = "image/*"
        return getIntent(intent, isNewTask)
    }

    /**
     * 获取分享图片的意图
     *
     * @param content    The content.
     * @param imagePaths The paths of images.
     * @return the intent of share images
     */
    fun getShareImageIntent(content: String, imagePaths: LinkedList<String>): Intent? {
        return getShareImageIntent(content, imagePaths, false)
    }

    /**
     * 获取分享图片的意图
     *
     * @param content    The content.
     * @param imagePaths The paths of images.
     * @param isNewTask  True to add flag of new task, false otherwise.
     * @return the intent of share images
     */
    fun getShareImageIntent(
        content: String,
        imagePaths: LinkedList<String>?,
        isNewTask: Boolean
    ): Intent? {
        if (imagePaths == null || imagePaths.isEmpty()) return null
        val files = ArrayList<File>()
        for (imagePath in imagePaths) {
            files.add(File(imagePath))
        }
        return getShareImageIntent(content, files, isNewTask)
    }

    /**
     * 获取分享图片的意图
     *
     * @param content The content.
     * @param images  The files of images.
     * @return the intent of share images
     */
    fun getShareImageIntent(content: String, images: List<File>): Intent? {
        return getShareImageIntent(content, images, false)
    }

    /**
     * 获取分享图片的意图
     *
     * @param content   The content.
     * @param images    The files of images.
     * @param isNewTask True to add flag of new task, false otherwise.
     * @return the intent of share images
     */
    fun getShareImageIntent(
        content: String,
        images: List<File>?,
        isNewTask: Boolean
    ): Intent? {
        if (images == null || images.isEmpty()) return null
        val uris = ArrayList<Uri>()
        for (image in images) {
            if (!image.isFile) continue
            val uri = file2Uri(image)
            uri?.let {
                uris.add(it)
            }
        }
        return getShareImageIntent(content, uris, isNewTask)
    }

    /**
     * 获取分享图片的意图
     *
     * @param content The content.
     * @param uris    The uris of images.
     * @return the intent of share images
     */
    fun getShareImageIntent(content: String, uris: ArrayList<Uri>): Intent {
        return getShareImageIntent(content, uris, false)
    }

    /**
     * 获取分享图片的意图
     *
     * @param content   The content.
     * @param uris      The uris of image.
     * @param isNewTask True to add flag of new task, false otherwise.
     * @return the intent of share image
     */
    fun getShareImageIntent(
        content: String,
        uris: ArrayList<Uri>,
        isNewTask: Boolean
    ): Intent {
        val intent = Intent(Intent.ACTION_SEND_MULTIPLE)
        intent.putExtra(Intent.EXTRA_TEXT, content)
        intent.putParcelableArrayListExtra(Intent.EXTRA_STREAM, uris)
        intent.type = "image/*"
        return getIntent(intent, isNewTask)
    }

    /**
     * 获取其他应用组件的意图
     *
     * @param packageName The name of the package.
     * @param className   The name of class.
     * @return the intent of component
     */
    fun getComponentIntent(packageName: String, className: String): Intent {
        return getComponentIntent(packageName, className, null, false)
    }

    /**
     * 获取其他应用组件的意图
     *
     * @param packageName The name of the package.
     * @param className   The name of class.
     * @param isNewTask   True to add flag of new task, false otherwise.
     * @return the intent of component
     */
    fun getComponentIntent(
        packageName: String,
        className: String,
        isNewTask: Boolean
    ): Intent {
        return getComponentIntent(packageName, className, null, isNewTask)
    }

    /**
     * 获取其他应用组件的意图
     *
     * @param packageName The name of the package.
     * @param className   The name of class.
     * @param bundle      The Bundle of extras to add to this intent.
     * @return the intent of component
     */
    fun getComponentIntent(
        packageName: String,
        className: String,
        bundle: Bundle
    ): Intent {
        return getComponentIntent(packageName, className, bundle, false)
    }

    /**
     * 获取其他应用组件的意图
     *
     * @param packageName The name of the package.
     * @param className   The name of class.
     * @param bundle      The Bundle of extras to add to this intent.
     * @param isNewTask   True to add flag of new task, false otherwise.
     * @return the intent of component
     */
    fun getComponentIntent(
        packageName: String,
        className: String,
        bundle: Bundle?,
        isNewTask: Boolean
    ): Intent {
        val intent = Intent(Intent.ACTION_VIEW)
        if (bundle != null) intent.putExtras(bundle)
        val cn = ComponentName(packageName, className)
        intent.component = cn
        return getIntent(intent, isNewTask)
    }

    /**
     * 获取关机的意图
     *
     * Requires root permission
     * or hold `android:sharedUserId="android.uid.system"`,
     * `<uses-permission android:name="android.permission.SHUTDOWN/>`
     * in manifest.
     *
     * @return the intent of shutdown
     */
    fun getShutdownIntent(): Intent {
        return getShutdownIntent(false)
    }

    /**
     * 获取关机的意图
     *
     * Requires root permission
     * or hold `android:sharedUserId="android.uid.system"`,
     * `<uses-permission android:name="android.permission.SHUTDOWN/>`
     * in manifest.
     *
     * @param isNewTask True to add flag of new task, false otherwise.
     * @return the intent of shutdown
     */
    fun getShutdownIntent(isNewTask: Boolean): Intent {
        val intent = Intent("android.intent.action.ACTION_REQUEST_SHUTDOWN")
        intent.putExtra("android.intent.extra.KEY_CONFIRM", false)
        return getIntent(intent, isNewTask)
    }

    /**
     * 获取拨号的意图
     *
     * @param phoneNumber The phone number.
     * @return the intent of dial
     */
    fun getDialIntent(phoneNumber: String): Intent {
        return getDialIntent(phoneNumber, false)
    }

    /**
     * 获取拨号的意图
     *
     * @param phoneNumber The phone number.
     * @param isNewTask   True to add flag of new task, false otherwise.
     * @return the intent of dial
     */
    fun getDialIntent(phoneNumber: String, isNewTask: Boolean): Intent {
        val intent = Intent(Intent.ACTION_DIAL, Uri.parse("tel:$phoneNumber"))
        return getIntent(intent, isNewTask)
    }

    /**
     * 获取电话的意图
     *
     * Must hold `<uses-permission android:name="android.permission.CALL_PHONE" />`
     *
     * @param phoneNumber The phone number.
     * @return the intent of call
     */
    @RequiresPermission(CALL_PHONE)
    fun getCallIntent(phoneNumber: String): Intent {
        return getCallIntent(phoneNumber, false)
    }

    /**
     * 获取电话的意图
     *
     * Must hold `<uses-permission android:name="android.permission.CALL_PHONE" />`
     *
     * @param phoneNumber The phone number.
     * @param isNewTask   True to add flag of new task, false otherwise.
     * @return the intent of call
     */
    @RequiresPermission(CALL_PHONE)
    fun getCallIntent(phoneNumber: String, isNewTask: Boolean): Intent {
        val intent = Intent("android.intent.action.CALL", Uri.parse("tel:$phoneNumber"))
        return getIntent(intent, isNewTask)
    }

    /**
     * 获取短信的意图
     *
     * @param phoneNumber The phone number.
     * @param content     The content of SMS.
     * @return the intent of send SMS
     */
    fun getSendSmsIntent(phoneNumber: String, content: String): Intent {
        return getSendSmsIntent(phoneNumber, content, false)
    }

    /**
     * 获取短信的意图
     *
     * @param phoneNumber The phone number.
     * @param content     The content of SMS.
     * @param isNewTask   True to add flag of new task, false otherwise.
     * @return the intent of send SMS
     */
    fun getSendSmsIntent(
        phoneNumber: String,
        content: String,
        isNewTask: Boolean
    ): Intent {
        val uri = Uri.parse("smsto:$phoneNumber")
        val intent = Intent(Intent.ACTION_SENDTO, uri)
        intent.putExtra("sms_body", content)
        return getIntent(intent, isNewTask)
    }

    /**
     * 获取拍照的意图
     *
     * @param outUri The uri of output.
     * @return the intent of capture
     */
    fun getCaptureIntent(outUri: Uri): Intent {
        return getCaptureIntent(outUri, false)
    }

    /**
     * 获取拍照的意图
     *
     * @param outUri    The uri of output.
     * @param isNewTask True to add flag of new task, false otherwise.
     * @return the intent of capture
     */
    fun getCaptureIntent(outUri: Uri, isNewTask: Boolean): Intent {
        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        intent.putExtra(MediaStore.EXTRA_OUTPUT, outUri)
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        return getIntent(intent, isNewTask)
    }

    private fun getIntent(intent: Intent, isNewTask: Boolean): Intent {
        return if (isNewTask) intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK) else intent
    }

    ///////////////////////////////////////////////////////////////////////////
    // other utils methods
    ///////////////////////////////////////////////////////////////////////////

    private fun getFileByPath(filePath: String): File? {
        return if (isSpace(filePath)) null else File(filePath)
    }

    private fun isSpace(s: String?): Boolean {
        if (s == null) return true
        var i = 0
        val len = s.length
        while (i < len) {
            if (!Character.isWhitespace(s[i])) {
                return false
            }
            ++i
        }
        return true
    }

    private fun file2Uri(file: File?): Uri? {
        if (file == null) return null
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            val authority =
                AppManager.getInstance().getApp().getPackageName() + ".utilcode.provider"
            return FileProvider.getUriForFile(AppManager.getInstance().getApp(), authority, file)
        } else {
            return Uri.fromFile(file)
        }
    }

//    /**
//     * 获取选择照片的 Intent
//     *
//     * @return
//     */
//    public static Intent getPickIntentWithGallery() {
//        Intent intent = new Intent(Intent.ACTION_PICK);
//        return intent.setType("image*//*");
//    }
//
//    /**
//     * 获取从文件中选择照片的 Intent
//     *
//     * @return
//     */
//    public static Intent getPickIntentWithDocuments() {
//        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
//        return intent.setType("image*//*");
//    }
//
//
//    public static Intent buildImageGetIntent(final Uri saveTo, final int outputX, final int outputY, final boolean returnData) {
//        return buildImageGetIntent(saveTo, 1, 1, outputX, outputY, returnData);
//    }
//
//    public static Intent buildImageGetIntent(Uri saveTo, int aspectX, int aspectY,
//                                             int outputX, int outputY, boolean returnData) {
//        Intent intent = new Intent();
//        if (Build.VERSION.SDK_INT < 19) {
//            intent.setAction(Intent.ACTION_GET_CONTENT);
//        } else {
//            intent.setAction(Intent.ACTION_OPEN_DOCUMENT);
//            intent.addCategory(Intent.CATEGORY_OPENABLE);
//        }
//        intent.setType("image*//*");
//        intent.putExtra("output", saveTo);
//        intent.putExtra("aspectX", aspectX);
//        intent.putExtra("aspectY", aspectY);
//        intent.putExtra("outputX", outputX);
//        intent.putExtra("outputY", outputY);
//        intent.putExtra("scale", true);
//        intent.putExtra("return-data", returnData);
//        intent.putExtra("outputFormat", Bitmap.CompressFormat.PNG.toString());
//        return intent;
//    }
//
//    public static Intent buildImageCropIntent(final Uri uriFrom, final Uri uriTo, final int outputX, final int outputY, final boolean returnData) {
//        return buildImageCropIntent(uriFrom, uriTo, 1, 1, outputX, outputY, returnData);
//    }
//
//    public static Intent buildImageCropIntent(Uri uriFrom, Uri uriTo, int aspectX, int aspectY,
//                                              int outputX, int outputY, boolean returnData) {
//        Intent intent = new Intent("com.android.camera.action.CROP");
//        intent.setDataAndType(uriFrom, "image*//*");
//        intent.putExtra("crop", "true");
//        intent.putExtra("output", uriTo);
//        intent.putExtra("aspectX", aspectX);
//        intent.putExtra("aspectY", aspectY);
//        intent.putExtra("outputX", outputX);
//        intent.putExtra("outputY", outputY);
//        intent.putExtra("scale", true);
//        intent.putExtra("return-data", returnData);
//        intent.putExtra("outputFormat", Bitmap.CompressFormat.PNG.toString());
//        return intent;
//    }
//
//    public static Intent buildImageCaptureIntent(final Uri uri) {
//        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
//        intent.putExtra(MediaStore.EXTRA_OUTPUT, uri);
//        return intent;
//    }
}