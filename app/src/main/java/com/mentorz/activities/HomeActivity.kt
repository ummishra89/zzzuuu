package com.mentorz.activities

import android.content.*
import android.os.Bundle
import android.support.design.widget.NavigationView
import android.support.v4.view.GravityCompat
import android.support.v7.app.ActionBarDrawerToggle
import android.text.TextUtils
import android.view.MenuItem
import android.view.View
import android.widget.ImageView
import android.widget.RelativeLayout
import com.mentorz.MentorzApplication
import com.mentorz.R
import com.mentorz.activities.authentication.AuthenticationActivity
import com.mentorz.database.DbManager
import com.mentorz.expertise.MentorExpertiseResponseListener
import com.mentorz.extensions.hideProgressBar
import com.mentorz.extensions.showProgressBar
import com.mentorz.extensions.showSnackBar
import com.mentorz.fragments.notification.NotificationFragment
import com.mentorz.interest.InterestPresenterImpl
import com.mentorz.interest.InterestView
import com.mentorz.interest.InterestsItem
import com.mentorz.listener.LogoutResponseListener
import com.mentorz.manager.BackStackManager
import com.mentorz.manager.TabManager
import com.mentorz.match.MatchFragment
import com.mentorz.me.UserProfileFragment
import com.mentorz.messages.HomeTabClickListener
import com.mentorz.messages.MessageFragment
import com.mentorz.model.ProfileData
import com.mentorz.retrofit.listeners.UserProfileListener
import com.mentorz.sinchvideo.BaseActivity
import com.mentorz.stories.StoriesFragment
import com.mentorz.utils.*
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_home.*
import org.jetbrains.anko.doAsync
import android.content.Context.MODE_PRIVATE
import android.content.pm.PackageManager
import android.content.pm.ResolveInfo
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import android.support.v7.app.AppCompatDelegate
import android.util.Log
import android.view.WindowManager
import com.craterzone.logginglib.manager.LoggerManager
import com.facebook.login.LoginManager
import com.linkedin.platform.LISessionManager
import com.mentorz.Manifest
import com.mentorz.adapter.HomePagerAdapter
import com.mentorz.customviews.ahbottomnavigation.AHBottomNavigation
import com.mentorz.customviews.ahbottomnavigation.AHBottomNavigationItem
import com.mentorz.customviews.ahbottomnavigation.notification.AHNotification
import com.mentorz.model.UpdateProfileRequest
import com.mentorz.pubnub.PubNubManagerService
import com.mentorz.requester.*
import com.mentorz.retrofit.listeners.UpdateProfileListener
import com.mentorz.uploadfile.FileType
import com.mentorz.uploadfile.FileUploadListener
import com.mentorz.uploadfile.FileUploadRequester
import io.vrinda.kotlinpermissions.PermissionCallBack
import java.io.*
import java.net.URL


class HomeActivity : BaseActivity(), View.OnClickListener, HomeTabClickListener, InterestView, NavigationView.OnNavigationItemSelectedListener, LogoutResponseListener, MentorExpertiseResponseListener, UserProfileListener, NotificationUtils.NotificationCountListener, NotificationFragment.ClearNotificationCountListener, DbManager.NotificationDataChangeListener, FileUploadListener, UpdateProfileListener {
    override fun updateProfileSuccess() {
        val intent = Intent(Constant.ACTION_UPDATE_USER_PROFILE)
        sendBroadcast(intent)
    }

    override fun updateProfileFail() {
        // showSnackBar(root, getString(R.string.unable_to_connect))
    }

    var profilePictureToken = ""

    override fun fileUploadSuccess(token: String, fileType: String) {
        runOnUiThread {
            when (fileType) {
                FileType.PROFILE_PICTURE -> {
                    profilePictureToken = token
                    updateProfileApi()
                    SignedUrlRequester(FileType.PROFILE_PICTURE, null, null, token).execute()
                }

            }
        }
    }

    override fun fileUploadFail() {
        // showSnackBar(root, getString(R.string.unable_to_connect))
    }

    private fun updateProfileApi() {
        val updateProfileRequest = UpdateProfileRequest()
        updateProfileRequest.name = MentorzApplication.instance?.prefs?.getUserName()
        updateProfileRequest.basicInfo = MentorzApplication.instance?.prefs?.getBasicInfo()
        updateProfileRequest.hresId = profilePictureToken
        updateProfileRequest.lresId = profilePictureToken
        updateProfileRequest.videoBioLres = MentorzApplication.instance?.prefs?.getProfileVideoLres()
        updateProfileRequest.videoBioHres = MentorzApplication.instance?.prefs?.getProfileVideoHres()
        val updateProfileRequester = UpdateProfileRequester(this, this, updateProfileRequest)
        updateProfileRequester.execute()
    }

    override fun onNotificationDataChange() {
        setUnseenNotificationCount()
    }

    override fun clearNotificationCount() {
        runOnUiThread {
            //            notificationCountView.visibility = View.GONE
        }
    }

    override fun onNotificationCountChange() {
        setUnseenNotificationCount()
    }

    private fun setUnseenNotificationCount() {
        doAsync {

            val count = dbManager?.getNotViewedNotificationCount()
            runOnUiThread {
                if (count == 0) {
                    bottom_navigation.setNotification("", 3)
//                    notificationCountView.visibility = View.GONE
                } else {
                    if (view_pager.currentItem == 3) {

                        dbManager!!.setAllViewedNotification();
                    } else {
                        bottom_navigation.setNotification("" + count, 3)
//                    notificationCountView.visibility = View.VISIBLE
//                    notificationCountText.text = count.toString()
                    }
                }
            }

        }
    }

    override fun onNetworkFail() {
        super.networkError()
    }

    override fun userProfileSuccess(userListItem: ProfileData) {
        runOnUiThread {
            setProfileImage()
        }

    }

    override fun onSessionExpired() {

        runOnUiThread {
            DialogUtils.
                    showDialog(this@HomeActivity, "", getString(R.string.session_expired_message), getString(R.string.ok), View.OnClickListener {
                        DialogUtils.dismiss()
                        startActivity(Intent(applicationContext, AuthenticationActivity::class.java))
                        finish()
                    })
        }
    }


    override fun onLogoutSuccess() {

        runOnUiThread {
            hideProgressBar(progressBar)
            Global.userInterests!!.clear()
            Global.defaultInterestsMap?.clear()
            Global.defaultExpertiseMap?.clear()
            Global.myexpertises!!.clear()
            MentorzApplication.instance?.prefs?.clear()
            doAsync {
                dbManager?.clear()
            }
            LoginManager.getInstance().logOut()
            LISessionManager.getInstance(getApplicationContext()).clearSession();
            val intent = Intent(applicationContext, AuthenticationActivity::class.java)
            startActivity(intent)
            finish()
        }
    }

    override fun onLogoutFail() {
        runOnUiThread {
            hideProgressBar(progressBar)
            showSnackBar(rootView, getString(R.string.something_went_wrong_please_try_later))
        }
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.account_settings -> {
                startActivity(Intent(this, AccountSettingActivity::class.java))
            }

            R.id.action_Feedback -> {
                startActivity(Intent(this, FeedbackActivity::class.java))
            }

            R.id.action_logout -> {
                DialogUtils.showDialog(this, getString(R.string.logout_title), getString(R.string.you_want_to_logout), getString(R.string.ok), getString(R.string.cancel), View.OnClickListener {
                    DialogUtils.dismiss()
                    showProgressBar(progressBar)
                    LogoutRequester(this@HomeActivity).execute()

                }, View.OnClickListener {
                    DialogUtils.dismiss()

                }
                )

            }
        }
        drawer_layout.closeDrawer(GravityCompat.START)
        return true
    }


    override fun onUpdateInterestSuccess() {

    }

    override fun onUpdateInterestFail() {
    }

    override fun showEmptyInterestAlert() {
    }

    override fun showProgress() {

    }

    override fun hideProgress() {
    }

    override fun setInterestAdapter(interest: List<InterestsItem>?) {
    }

    override fun onClickMatch(any: Any?) {

        view_pager.setCurrentItem(1, false)
        bottom_navigation.setCurrentItem(1, false)

        (adapter?.getItem(1) as MatchFragment).openGetMentorTab()

        /*setMatchTab()
        if (mTabManager?.getmLastTabFragment() != null)
            (mTabManager?.getmLastTabFragment() as MatchFragment).openGetMentorTab()*/
    }

    override fun onClickMessage(any: Any?) {
    }

    override fun onClickStories(any: Any?) {
    }

    override fun onClickNotification(any: Any?) {
    }

    override fun onClickMe(any: Any?) {
    }

    override fun onResume() {
        super.onResume()

        setUnseenNotificationCount()
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when (requestCode) {
            EDIT_PROFILE_REQUEST_CODE -> {
                setProfileImage()
            }
        }
    }

    override fun onClick(p0: View?) {
        when (p0?.id) {
//            lStories.id -> {
//                invalidateOptionsMenu()
//                setStoriesTab()
//            }
//            lMatch.id -> {
//                invalidateOptionsMenu()
//                setMatchTab()
//
//            }

            R.id.rEditProfile -> {
                startActivityForResult(Intent(this, EditProfileActivity::class.java), EDIT_PROFILE_REQUEST_CODE)
            }


//            lMessages.id -> {
//                setMessagesTab()
//
//                // startActivity(Intent(this,ChatActivity::class.java))
//
//            }
//            lNotification.id -> {
//                invalidateOptionsMenu()
//
//                imgStories.isSelected = false
//                txtStories.isSelected = false
//
//                imgMatch.isSelected = false
//                txtMatch.isSelected = false
//
//                imgMessage.isSelected = false
//                txtMessage.isSelected = false
//
//                imgNotification.isSelected = true
//                txtNotification.isSelected = true
//
//                imgMe.isSelected = false
//                txtMe.isSelected = false
//
//                mTabManager!!.onTabChanged(FragmentTag.NOTIFICATION)
//                backStackManager!!.currentTab = FragmentTag.NOTIFICATION
//
//            }
//            lMe.id -> {
//                invalidateOptionsMenu()
//                setMeTab()
//
//            }
        }
    }

//    private fun setMessagesTab() {
//        invalidateOptionsMenu()
//
//        imgStories.isSelected = false
//        txtStories.isSelected = false
//
//        imgMatch.isSelected = false
//        txtMatch.isSelected = false
//
//        imgMessage.isSelected = true
//        txtMessage.isSelected = true
//
//        imgNotification.isSelected = false
//        txtNotification.isSelected = false
//
//        imgMe.isSelected = false
//        txtMe.isSelected = false
//        // FragmentFactory.replaceFragment(MessageFragment(), R.id.frameContainer, this@HomeActivity)
//        mTabManager!!.onTabChanged(FragmentTag.MESSAGE)
//        backStackManager!!.currentTab = FragmentTag.MESSAGE
//
//    }

//    fun setStoriesTab() {
//        imgStories.isSelected = true
//        txtStories.isSelected = true
//
//        imgMatch.isSelected = false
//        txtMatch.isSelected = false
//
//        imgMessage.isSelected = false
//        txtMessage.isSelected = false
//
//        imgNotification.isSelected = false
//        txtNotification.isSelected = false
//
//        imgMe.isSelected = false
//        txtMe.isSelected = false
//        mTabManager!!.onTabChanged(FragmentTag.STORIES)
//        backStackManager!!.currentTab = FragmentTag.STORIES
//        // FragmentFactory.replaceFragment(StoriesFragment(), R.id.frameContainer, this)
//
//
//    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)

//        view_pager.setCurrentItem(1,false)
//        bottom_navigation.setCurrentItem(1,false)


//        setMatchTab()

        if (intent?.action.equals("OPEN_TAB_REQUEST")) {

//            setMessagesTab()
            view_pager.setCurrentItem(2, false)
            bottom_navigation.setCurrentItem(2, false)
            (adapter?.getItem(2) as MessageFragment).openRequests()
//            if (mTabManager?.getmLastTabFragment() != null)
//                (mTabManager?.getmLastTabFragment() as MessageFragment).openRequests()

        }

    }

//    fu#n setMatchTab() {
//        imgStories.isSelected = false
//        txtStories.isSelected = false
//
//        imgMatch.isSelected = true
//        txtMatch.isSelected = true
//
//        imgMessage.isSelected = false
//        txtMessage.isSelected = false
//
//        imgNotification.isSelected = false
//        txtNotification.isSelected = false
//
//        imgMe.isSelected = false
//        txtMe.isSelected = false
//        //FragmentFactory.replaceFragment(MatchFragment(), R.id.frameContainer, this@HomeActivity)
//        mTabManager!!.onTabChanged(FragmentTag.MATCH)
//        backStackManager!!.currentTab = FragmentTag.MATCH
//
//
//    }

//    fun setMeTab() {
//        imgStories.isSelected = false
//        txtStories.isSelected = false
//        imgMatch.isSelected = false
//        txtMatch.isSelected = false
//
//        imgMessage.isSelected = false
//        txtMessage.isSelected = false
//
//        imgNotification.isSelected = false
//        txtNotification.isSelected = false
//
//        imgMe.isSelected = true
//        txtMe.isSelected = true
//
//        mTabManager!!.onTabChanged(FragmentTag.ME)
//        backStackManager!!.currentTab = FragmentTag.ME
//
//        // FragmentFactory.replaceFragment(UserProfileFragment.getInstance(MentorzApplication.instance?.prefs?.getUserId()!!), R.id.frameContainer, this)
//
//    }


    private var interestPresenter = InterestPresenterImpl(this)
    private val EDIT_PROFILE_REQUEST_CODE = 100
    //    private var mTabManager: TabManager? = null
    private var backStackManager: BackStackManager? = null
    private var dbManager: DbManager? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val enabledTranslucentNavigation = getSharedPreferences("shared", Context.MODE_PRIVATE)
                .getBoolean("translucentNavigation", false)
        setTheme(if (enabledTranslucentNavigation) R.style.AppTheme_TranslucentNavigation else R.style.AppTheme)
        backStackManager?.clear()
        //checkForAutoStartServicePermission()
        interestPresenter.getUserInterests()
        dbManager = DbManager.getInstance(this@HomeActivity)
        setContentView(R.layout.activity_home)
        setSupportActionBar(toolbar)
        uploadProfileImageForSocialLogin()
        addUIListener()
//        createTabs()
//        setStoriesTab()
        //  setUnseenNotificationCount()
        startService(Intent(applicationContext, PubNubManagerService::class.java))
        NotificationFragment.registerClearNotificationCountListener(this)
        MentorBioRequester(MentorzApplication.instance?.prefs?.getUserId()).execute()
        NotificationUtils.registerNotificationCountListener(this)
        MyProfileRequester(this).execute()
        val toggle = ActionBarDrawerToggle(
                this, drawer_layout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close)
        drawer_layout.addDrawerListener(toggle)
        toggle.syncState()
        nav_view.setNavigationItemSelectedListener(this)

        // send logs
        val headerView = nav_view.getHeaderView(0)
        headerView.findViewById<ImageView>(R.id.profileImage).setOnLongClickListener {
            val filePath = LoggerManager.getInstance(this@HomeActivity).diagnosticsFilePath
            val send = Intent(android.content.Intent.ACTION_SEND)
            send.type = "*/*"
            send.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(File(filePath)))
            startActivity(Intent.createChooser(send, "send logs"))
            true
        }
        headerView.findViewById<ImageView>(R.id.profileImage).setOnClickListener(View.OnClickListener {
            startActivityForResult(Intent(this, EditProfileActivity::class.java), 0)

        })

        MentorExpertiseRequester(MentorzApplication.instance?.prefs?.getUserId(), null, this).execute()
        init()

        val intentFilter = IntentFilter()
        intentFilter.addAction(Constant.ACTION_UPDATE_USER_PROFILE)
        intentFilter.addAction(Constant.ACTION_PUB_COUNT_UNREAD_COUNT)

        registerReceiver(receiver, intentFilter)


    }

    fun chatUnreadCountUpdate(count: Int?) {
        runOnUiThread {
            if (count!! > 0 && view_pager.currentItem != 2) {
                bottom_navigation.setNotification("" + count, 2)
            } else {
                bottom_navigation.setNotification("", 2)
            }
        }

    }

    var receiver = object : BroadcastReceiver() {
        override fun onReceive(p0: Context?, intent: Intent?) {
            when (intent?.action) {
                Constant.ACTION_UPDATE_USER_PROFILE -> {
                    setProfileImage()
                }
                Constant.ACTION_PUB_COUNT_UNREAD_COUNT -> {
                    chatUnreadCountUpdate(intent.getIntExtra("count", 0));
                }
            }

        }

    }

    fun uploadProfileImageForSocialLogin() {
        if (MentorzApplication.instance?.prefs?.isSocialLogin()!!) {
            uploadImage(Uri.parse(LoggerManager.logFilePath() + "/profile_pic.png"), FileType.PROFILE_PICTURE)
            // downloadSocialProfileImage(MentorzApplication.instance?.prefs?.getProfilePictureLres())
        }

    }

    private val bottomNavigationItems = ArrayList<AHBottomNavigationItem>()
    private var adapter: HomePagerAdapter? = null

    private fun init() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            AppCompatDelegate.setCompatVectorFromResourcesEnabled(true);
        }

        val item1 = AHBottomNavigationItem(R.string.stories, R.drawable.unselected_megaphone, R.color.colorAccent)
        val item2 = AHBottomNavigationItem(R.string.match, R.drawable.unselected_match, R.color.colorAccent)
        val item3 = AHBottomNavigationItem(R.string.messages, R.drawable.unselected_messages, R.color.colorAccent)
        val item4 = AHBottomNavigationItem(R.string.notification, R.drawable.notification, R.color.colorAccent)
        val item5 = AHBottomNavigationItem(R.string.me, R.drawable.unselected_me, R.color.colorAccent)


        bottomNavigationItems.add(item1)
        bottomNavigationItems.add(item2)
        bottomNavigationItems.add(item3)
        bottomNavigationItems.add(item4)
        bottomNavigationItems.add(item5)


        bottom_navigation.addItems(bottomNavigationItems)


        bottom_navigation.setOnTabSelectedListener { position, wasSelected ->
            view_pager.setCurrentItem(position, false)
            if (position == 3) {
                resetNotificationCount()
            }
            if (position == 2) {
                chatUnreadCountUpdate(0)
                updateSearchIconState()
            }
            true
        }



        view_pager.offscreenPageLimit = 5;
        adapter = HomePagerAdapter(supportFragmentManager)
        view_pager.adapter = adapter
        view_pager.setCurrentItem(0, false)
        bottom_navigation.setCurrentItem(0, false)
        bottom_navigation.setNotification("", 2)

        // showNotificaton();
    }

    private fun updateSearchIconState() {

        (adapter?.getItem(2) as MessageFragment).updateSearchIconState()

    }

    private fun resetNotificationCount() {
        //  doAsync {
        dbManager!!.setAllViewedNotification()
        //  }
        setUnseenNotificationCount()

    }


    /*private fun createTabs() {
        mTabManager = TabManager(this@HomeActivity, R.id.frameContainer)
        backStackManager = BackStackManager.getInstance()
        if (backStackManager!!.backStack.isEmpty()) {
            backStackManager!!.initBackStack()
        }
        mTabManager!!.addTab(FragmentTag.STORIES, StoriesFragment::class.java, null)
        mTabManager!!.addTab(FragmentTag.MATCH, MatchFragment::class.java, null)
        mTabManager!!.addTab(FragmentTag.MESSAGE, MessageFragment::class.java, null)
        mTabManager!!.addTab(FragmentTag.ME, UserProfileFragment::class.java, null)
        mTabManager!!.addTab(FragmentTag.NOTIFICATION, NotificationFragment::class.java, null)
    }*/

    fun addUIListener() {
        // DbManager.getInstance(this)
//        lStories.setOnClickListener(this)
//        lMatch.setOnClickListener(this)
//        lMessages.setOnClickListener(this)
//        lNotification.setOnClickListener(this)
//        lMe.setOnClickListener(this)
        val headerView = nav_view.getHeaderView(0)
        headerView.findViewById<RelativeLayout>(R.id.rEditProfile).setOnClickListener(this)
        setProfileImage()

    }

    private fun setProfileImage() {
        val headerView = nav_view.getHeaderView(0)
        if (!TextUtils.isEmpty(MentorzApplication.instance?.prefs?.getProfilePictureLres())) {
            Picasso.with(this@HomeActivity).load(MentorzApplication.instance?.prefs?.getProfilePictureLres()).resize(300, 300).centerCrop().placeholder(R.drawable.default_avatar).into(headerView.findViewById<ImageView>(R.id.profileImage))
        }

    }


    /* fun downloadSocialProfileImage(url :String?){
         doAsync {
             try{
                 val url = URL(url)
                 val input : InputStream?= BufferedInputStream(url.openStream());
                 val out : ByteArrayOutputStream = ByteArrayOutputStream();
                 var buf : ByteArray = ByteArray(1024) ;
                 var n : Int = input!!.read(buf)
                 while (n!=-1)
                 {
                     out.write(buf, 0, n);
                     n=input!!.read(buf)
                 }
                 out.close();
                 input.close();
                 var  response :ByteArray = out.toByteArray()
                 val fos = FileOutputStream(""+LoggerManager.logFilePath()+"/profile_pic.png")
                 fos.write(response)
                 fos.close()

                 uploadImage(Uri.parse(LoggerManager.logFilePath()+"/profile_pic.png"), FileType.PROFILE_PICTURE)
             }catch (exception :Exception){
                 Log.e("LoginPresenterImpl",""+exception.toString())
             }
             finally {

             }


         }
     }*/


    private fun uploadImage(fileUri: Uri, fileType: String) {
        val filePath = fileUri.path
        FileUploadRequester(this, filePath, "image/png", fileType).execute()
    }

    override fun onBackPressed() {
        if (drawer_layout.isDrawerOpen(GravityCompat.START)) {
            drawer_layout.closeDrawer(GravityCompat.START)
        } else {
            finish()
        }
    }

    override fun onDestroy() {
        startService(Intent(applicationContext, PubNubManagerService::class.java))
        super.onDestroy()
    }

    fun checkForAutoStartServicePermission() {
        try {
            var intent = Intent();
            var a = ComponentName("com.miui.securitycenter", "com.miui.permcenter.autostart.AutoStartManagementActivity")
            var manufacturer = android.os.Build.MANUFACTURER;
            if (manufacturer == "Xiaomi") {
                intent.setComponent(ComponentName("com.miui.securitycenter", "com.miui.permcenter.autostart.AutoStartManagementActivity"))
            }
            if (manufacturer == "oppo") {
                intent.setComponent(ComponentName("com.coloros.safecenter", "com.coloros.safecenter.permission.startup.StartupAppListActivity"))
            }
            if (manufacturer == "vivo") {
                intent.setComponent(ComponentName("com.vivo.permissionmanager", "com.vivo.permissionmanager.activity.BgStartUpManagerActivity"))
            }

            var list: List<ResolveInfo> = getPackageManager().queryIntentActivities(intent, PackageManager.MATCH_DEFAULT_ONLY);
            if (list.size > 0) {
                startActivity(intent);
            }
        } catch (e: Exception) {
            Log.e("" + TAG, "" + e.toString())
        }
    }



}


