package com.nextdns.manager

import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.google.gson.Gson
import kotlinx.coroutines.*

class MainActivity : AppCompatActivity() {
    companion object {
        const val API_KEY = "541cf0ce44b4b72fa00c4082902a2c22798747db"
        const val PROFILE_ID = "969733"
        const val BASE_URL = "https://api.nextdns.io"
        val SEC = mapOf("threatIntelligenceFeeds" to "🔬 威胁情报源","aiThreatDetection" to "🤖 AI威胁检测","googleSafeBrowsing" to "🛡️ Google安全浏览","cryptojacking" to "💰 加密货币挖矿","dnsRebinding" to "🔗 DNS重新绑定","idnHomographs" to "🔤 IDN同形字","typosquatting" to "✏️ 拼写错误","dga" to "🔄 DGA检测","nrd" to "🌐 NRD检测","ddns" to "📡 DDNS检测","parking" to "🅿️ 停放检测","csam" to "🚫 CSAM")
        val PC_SVC = mapOf("tiktok" to "🎵 抖音","facebook" to "👥 Facebook","instagram" to "📸 Instagram","youtube" to "▶️ YouTube")
        val PC_CAT = mapOf("porn" to "🔞 成人","gambling" to "🎰 赌博","social-networks" to "📱 社交")
    }
    private lateinit var configNameText: TextView; private lateinit var statsText: TextView
    private lateinit var refreshBtn: Button; private lateinit var progressBar: ProgressBar
    private lateinit var statusText: TextView
    private lateinit var secCon: LinearLayout; private lateinit var privCon: LinearLayout
    private lateinit var parCon: LinearLayout; private lateinit var denCon: LinearLayout
    private lateinit var alCon: LinearLayout; private lateinit var setCon: LinearLayout
    private lateinit var scrollView: ScrollView
    private val gson = Gson(); private val scope = CoroutineScope(Dispatchers.Main + SupervisorJob())

    override fun onCreate(s: Bundle?) { super.onCreate(s); setContentView(R.layout.activity_main)
        configNameText = findViewById(R.id.configNameText); statsText = findViewById(R.id.statsText)
        refreshBtn = findViewById(R.id.refreshBtn); progressBar = findViewById(R.id.progressBar)
        statusText = findViewById(R.id.statusText); scrollView = findViewById(R.id.scrollView)
        secCon = findViewById(R.id.securityContainer); privCon = findViewById(R.id.privacyContainer)
        parCon = findViewById(R.id.parentalContainer); denCon = findViewById(R.id.denylistContainer)
        alCon = findViewById(R.id.allowlistContainer); setCon = findViewById(R.id.settingsContainer)
        refreshBtn.setOnClickListener { load() }; load() }

    private fun load() { pb(true); st("加载中...")
        scope.launch { try {
            val c = gson.fromJson(io { apiGet("/profiles/$PROFILE_ID") }, NextDNSConfig::class.java)
            val a = try { gson.fromJson(io { apiGet("/profiles/$PROFILE_ID/analytics/status") }, AnalyticsResponse::class.java) } catch(e: Exception) { null }
            val d = try { gson.fromJson(io { apiGet("/profiles/$PROFILE_ID/denylist?limit=50") }, DomainListResponse::class.java) } catch(e: Exception) { null }
            val al = try { gson.fromJson(io { apiGet("/profiles/$PROFILE_ID/allowlist?limit=50") }, DomainListResponse::class.java) } catch(e: Exception) { null }
            withContext(Dispatchers.Main) { show(c, a, d, al); pb(false); st("✅ 加载成功") }
        } catch(e: Exception) { withContext(Dispatchers.Main) { pb(false); st("❌ ${e.message}") } } }

    private fun show(c: NextDNSConfig, a: AnalyticsResponse?, d: DomainListResponse?, al: DomainListResponse?) {
        configNameText.text = "📋 ${c.data.name}"
        if (a != null) { val t=a.data.sumOf { it.queries }; val b=a.data.find { it.status=="blocked" }?.queries ?: 0; statsText.text = "📊 查询:$t 拦截:$b" }
        secCon.removeAllViews(); listOf("threatIntelligenceFeeds","aiThreatDetection","googleSafeBrowsing","cryptojacking","dnsRebinding","idnHomographs","typosquatting","dga","nrd","ddns","parking","csam").forEach { k ->
            val v = when(k) { "threatIntelligenceFeeds"->c.data.security.threatIntelligenceFeeds; "aiThreatDetection"->c.data.security.aiThreatDetection; "googleSafeBrowsing"->c.data.security.googleSafeBrowsing; "cryptojacking"->c.data.security.cryptojacking; "dnsRebinding"->c.data.security.dnsRebinding; "idnHomographs"->c.data.security.idnHomographs; "typosquatting"->c.data.security.typosquatting; "dga"->c.data.security.dga; "nrd"->c.data.security.nrd; "ddns"->c.data.security.ddns; "parking"->c.data.security.parking; else->c.data.security.csam }
            secCon.addView(sw(SEC[k]?:k, v) { _, b -> patch("/profiles/$PROFILE_ID", """{"security":{"$k":$b}}""") { st("✅ ${SEC[k]}已${if(b)"启用"else"关闭"}") } })}
        privCon.removeAllViews(); privCon.addView(sw("🚫 拦截伪装跟踪器", c.data.privacy.disguisedTrackers) { _, b -> patch("/profiles/$PROFILE_ID", """{"privacy":{"disguisedTrackers":$b}}""") { st("✅ 已${if(b)"启用"else"关闭"}") } })
        privCon.addView(sw("💰 联盟链接", c.data.privacy.allowAffiliate) { _, b -> patch("/profiles/$PROFILE_ID", """{"privacy":{"allowAffiliate":$b}}""") { st("✅ 已${if(b)"启用"else"关闭"}") } })
        privCon.addView(TextView(this).apply { text = "📦 拦截列表:${c.data.privacy.blocklists.size}个"; setPadding(0,16,0,8) })
        privCon.addView(Button(this).apply { text = "➕ 添加列表"; setOnClickListener { addBlocklistDialog() } })
        parCon.removeAllViews(); c.data.parentalControl.services.forEach { svc -> parCon.addView(sw(PC_SVC[svc.id]?:svc.id, svc.active) { _, b -> patch("/profiles/$PROFILE_ID/parentalControl/services/${svc.id}", """{"active":$b}""") { st("✅ 已${if(b)"启用"else"关闭"}") } }) }
        c.data.parentalControl.categories.forEach { cat -> parCon.addView(sw(PC_CAT[cat.id]?:cat.id, cat.active) { _, b -> patch("/profiles/$PROFILE_ID/parentalControl/categories/${cat.id}", """{"active":$b}""") { st("✅ 已${if(b)"启用"else"关闭"}") } }) }
        parCon.addView(sw("🔒 安全搜索", c.data.parentalControl.safeSearch) { _, b -> patch("/profiles/$PROFILE_ID/parentalControl", """{"safeSearch":$b}""") { st("✅ 已${if(b)"启用"else"关闭"}") } })
        parCon.addView(sw("📺 YouTube限制", c.data.parentalControl.youtubeRestrictedMode) { _, b -> patch("/profiles/$PROFILE_ID/parentalControl", """{"youtubeRestrictedMode":$b}""") { st("✅ 已${if(b)"启用"else"关闭"}") } })
        setCon.removeAllViews(); setCon.addView(sw("📝 DNS日志", c.data.settings.logs.enabled) { _, b -> patch("/profiles/$PROFILE_ID/settings/logs", """{"enabled":$b}""") { st("✅ 日志已${if(b)"启用"else"关闭"}") } })
        setCon.addView(sw("🚧 拦截页面", c.data.settings.blockPage.enabled) { _, b -> patch("/profiles/$PROFILE_ID/settings/blockPage", """{"enabled":$b}""") { st("✅ 拦截页${if(b)"启用"else"关闭"}") } })
        setCon.addView(sw("⚡ 缓存增强", c.data.settings.performance.cacheBoost) { _, b -> patch("/profiles/$PROFILE_ID/settings/performance", """{"cacheBoost":$b}""") { st("✅ 已${if(b)"启用"else"关闭"}") } })
        setCon.addView(sw("🔀 CNAME扁平化", c.data.settings.performance.cnameFlattening) { _, b -> patch("/profiles/$PROFILE_ID/settings/performance", """{"cnameFlattening":$b}""") { st("✅ 已${if(b)"启用"else"关闭"}") } })
        setCon.addView(sw("🌐 Web3", c.data.settings.web3) { _, b -> patch("/profiles/$PROFILE_ID/settings", """{"web3":$b}""") { st("✅ Web3已${if(b)"启用"else"关闭"}") } })
        denCon.removeAllViews(); showDomains(denCon, d, "denylist"); alCon.removeAllViews(); showDomains(alCon, al, "allowlist") }

    private fun showDomains(con: LinearLayout, data: DomainListResponse?, type: String) {
        if (data == null || data.data.isEmpty()) con.addView(TextView(this).apply { text = "（空）"; setPadding(16,8,16,8) })
        else data.data.forEach { item ->
            val row = LinearLayout(this).apply { orientation = HORIZONTAL; layoutParams = LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT) }
            row.addView(sw(item.id, item.active) { _, b -> patch("/profiles/$PROFILE_ID/$type/${item.id}", """{"active":$b}""") { st("✅ ${item.id}已${if(b)"启用"else"关闭"}") } })
            row.addView(Button(this).apply { text = "🗑️"; setOnClickListener { delDomain(type, item.id) } })
            con.addView(row) }
        con.addView(Button(this).apply { text = if(type=="denylist") "🚫 添加黑名单" else "✅ 添加白名单"; setOnClickListener { addDomainDialog(type) } }) }

    private fun addDomainDialog(type: String) { val inp = EditText(this).apply { hint = "输入域名" }; AlertDialog.Builder(this).setTitle(if(type=="denylist") "添加黑名单" else "添加白名单").setView(inp).setPositiveButton("添加") { _, _ -> inp.text.toString().trim().takeIf { it.isNotEmpty() }?.let { addDomain(type, it) } }.setNegativeButton("取消", null).show() }
    private fun addBlocklistDialog() { val inp = EditText(this).apply { hint = "输入列表ID，如 oisd" }; AlertDialog.Builder(this).setTitle("添加拦截列表").setMessage("常用: oisd, adguarddns, easyprivacy, sunlight").setView(inp).setPositiveButton("添加") { _, _ -> inp.text.toString().trim().takeIf { it.isNotEmpty() }?.let { addBlocklist(it) } }.setNegativeButton("取消", null).show() }

    private fun sw(label: String, checked: Boolean, cb: (CompoundButton, Boolean) -> Unit): Switch = Switch(this).apply { text = label; isChecked = checked; setOnCheckedChangeListener(cb); layoutParams = LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT).apply { setMargins(0,8,0,8) } }
    private fun pb(show: Boolean) { progressBar.visibility = if(show) View.VISIBLE else View.GONE }
    private fun st(msg: String) { statusText.text = msg; scrollView.smoothScrollTo(0,0) }

    private fun <T> io(block: () -> T): T = runBlocking(Dispatchers.IO) { block() }
    private fun patch(path: String, body: String, cb: () -> Unit) { scope.launch { try { io { apiPatch(path, body) }; withContext(Dispatchers.Main) { cb() } } catch(e: Exception) { withContext(Dispatchers.Main) { st("❌ ${e.message}"); load() } } } }
    private fun addDomain(type: String, domain: String) { scope.launch { try { io { apiPost("/profiles/$PROFILE_ID/$type", """{"id":"$domain"}""") }; withContext(Dispatchers.Main) { st("✅ $domain 已添加"); load() } } catch(e: Exception) { withContext(Dispatchers.Main) { st("❌ ${e.message}") } } }
    private fun delDomain(type: String, domain: String) { scope.launch { try { io { apiDel("/profiles/$PROFILE_ID/$type/$domain") }; withContext(Dispatchers.Main) { st("✅ $domain 已删除"); load() } } catch(e: Exception) { withContext(Dispatchers.Main) { st("❌ ${e.message}") } } } }
    private fun addBlocklist(id: String) { scope.launch { try { io { apiPost("/profiles/$PROFILE_ID/privacy/blocklists", """{"id":"$id"}""") }; withContext(Dispatchers.Main) { st("✅ 列表 $id 已添加"); load() } } catch(e: Exception) { withContext(Dispatchers.Main) { st("❌ ${e.message}") } } }

    private fun apiGet(path: String): String { val c = java.net.URL("$BASE_URL$path").openConnection() as java.net.HttpURLConnection; c.setRequestProperty("X-Api-Key", API_KEY); return c.inputStream.bufferedReader().readText() }
    private fun apiPatch(path: String, body: String): String { val c = java.net.URL("$BASE_URL$path").openConnection() as java.net.HttpURLConnection; c.requestMethod = "PATCH"; c.setRequestProperty("X-Api-Key", API_KEY); c.setRequestProperty("Content-Type", "application/json"); c.doOutput = true; c.outputStream.write(body.toByteArray()); return if(c.responseCode in 200..299) "" else throw Exception("Error ${c.responseCode}") }
    private fun apiPost(path: String, body: String): String { val c = java.net.URL("$BASE_URL$path").openConnection() as java.net.HttpURLConnection; c.requestMethod = "POST"; c.setRequestProperty("X-Api-Key", API_KEY); c.setRequestProperty("Content-Type", "application/json"); c.doOutput = true; c.outputStream.write(body.toByteArray()); return if(c.responseCode in 200..299) "" else throw Exception("Error ${c.responseCode}") }
    private fun apiDel(path: String): String { val c = java.net.URL("$BASE_URL$path").openConnection() as java.net.HttpURLConnection; c.requestMethod = "DELETE"; c.setRequestProperty("X-Api-Key", API_KEY); return if(c.responseCode in 200..299) "" else throw Exception("Error ${c.responseCode}") }

    override fun onDestroy() { super.onDestroy(); scope.cancel() }
}

// 数据类
data class NextDNSConfig(val data: ConfigData)
data class ConfigData(val id: String, val name: String, val fingerprint: String, val security: Security, val privacy: Privacy, val parentalControl: ParentalControl, val settings: Settings)
data class Security(val threatIntelligenceFeeds: Boolean, val aiThreatDetection: Boolean, val googleSafeBrowsing: Boolean, val cryptojacking: Boolean, val dnsRebinding: Boolean, val idnHomographs: Boolean, val typosquatting: Boolean, val dga: Boolean, val nrd: Boolean, val ddns: Boolean, val parking: Boolean, val csam: Boolean)
data class Privacy(val disguisedTrackers: Boolean, val allowAffiliate: Boolean, val blocklists: List<Blocklist>)
data class Blocklist(val id: String, val name: String?, val entries: Int)
data class ParentalControl(val services: List<Service>, val categories: List<Category>, val safeSearch: Boolean, val youtubeRestrictedMode: Boolean, val blockBypass: Boolean)
data class Service(val id: String, val active: Boolean)
data class Category(val id: String, val active: Boolean)
data class Logs(val enabled: Boolean)
data class BlockPage(val enabled: Boolean)
data class Performance(val ecs: Boolean, val cacheBoost: Boolean, val cnameFlattening: Boolean)
data class Settings(val logs: Logs, val blockPage: BlockPage, val performance: Performance, val web3: Boolean)
data class StatusData(val status: String, val queries: Int)
data class AnalyticsResponse(val data: List<StatusData>)
data class DomainItem(val id: String, val active: Boolean)
data class DomainListResponse(val data: List<DomainItem>)