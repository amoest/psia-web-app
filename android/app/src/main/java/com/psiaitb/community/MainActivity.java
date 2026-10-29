package com.psiaitb.community;

import android.os.Bundle;
import android.view.ViewGroup;
import android.webkit.WebView;

import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.getcapacitor.BridgeActivity;
import com.getcapacitor.WebViewListener;

/**
 * Wrapper PS IA-ITB.
 *
 * Perilaku bawaan Capacitor yang dipakai apa adanya:
 *  - Tombol Back: mundur di riwayat WebView, keluar app bila riwayat habis (plugin @capacitor/app).
 *  - Link ke domain di luar allowNavigation (Instagram, WhatsApp, Google, dll.) dibuka di app/browser sistem.
 *  - Halaman gagal dimuat -> www/offline.html (server.errorPath).
 *  - Input file (upload foto saat registrasi) ditangani BridgeWebChromeClient.
 *
 * Tambahan di sini: pull-to-refresh, hanya aktif saat halaman sudah di posisi paling atas.
 */
public class MainActivity extends BridgeActivity {

    private SwipeRefreshLayout swipeRefresh;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        attachPullToRefresh();
    }

    private void attachPullToRefresh() {
        final WebView webView = getBridge().getWebView();
        if (webView == null || !(webView.getParent() instanceof ViewGroup)) {
            return;
        }

        ViewGroup parent = (ViewGroup) webView.getParent();
        int index = parent.indexOfChild(webView);
        ViewGroup.LayoutParams params = webView.getLayoutParams();
        parent.removeView(webView);

        swipeRefresh = new SwipeRefreshLayout(this);
        swipeRefresh.addView(webView, new ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        parent.addView(swipeRefresh, index, params);

        swipeRefresh.setColorSchemeColors(0xFF2FD07A);
        swipeRefresh.setProgressBackgroundColorSchemeColor(0xFF0F1830);

        // Jangan picu refresh selama halaman masih bisa di-scroll ke atas.
        swipeRefresh.setOnChildScrollUpCallback((p, child) ->
                webView.getScrollY() > 0 || webView.canScrollVertically(-1));

        swipeRefresh.setOnRefreshListener(webView::reload);

        getBridge().addWebViewListener(new WebViewListener() {
            @Override
            public void onPageLoaded(WebView view) {
                if (swipeRefresh != null) {
                    swipeRefresh.setRefreshing(false);
                }
            }
        });
    }
}
