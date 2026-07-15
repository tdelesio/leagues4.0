/**
 * MakeYourPicks Premium Mobile-Friendly Notification & Confirmation System
 * Automatically overrides window.alert with high-end glassmorphic Toast notifications,
 * and provides window.customConfirm for beautiful, smooth modal dialogs.
 */
(function() {
    // 1. Inject Glassmorphic Stylesheet
    var style = document.createElement('style');
    style.innerHTML = `
        /* Premium Toast CSS System */
        .toast-container {
            position: fixed;
            bottom: 24px;
            left: 50%;
            transform: translateX(-50%);
            z-index: 999999;
            display: flex;
            flex-direction: column;
            gap: 10px;
            width: calc(100% - 32px);
            max-width: 420px;
            pointer-events: none;
        }
        .toast-notification {
            padding: 14px 18px;
            border-radius: 14px;
            background: rgba(25, 25, 32, 0.85);
            backdrop-filter: blur(20px);
            -webkit-backdrop-filter: blur(20px);
            border: 1px solid rgba(255, 255, 255, 0.12);
            color: #ffffff;
            box-shadow: 0 12px 40px rgba(0, 0, 0, 0.35);
            font-family: -apple-system, BlinkMacSystemFont, "Segoe UI", Roboto, Helvetica, Arial, sans-serif;
            font-size: 14px;
            font-weight: 500;
            line-height: 1.5;
            pointer-events: auto;
            cursor: pointer;
            display: flex;
            align-items: center;
            gap: 12px;
            transform: translateY(30px) scale(0.95);
            opacity: 0;
            transition: all 0.4s cubic-bezier(0.165, 0.84, 0.44, 1);
        }
        .toast-notification.show {
            transform: translateY(0) scale(1);
            opacity: 1;
        }
        .toast-notification.toast-success {
            border-left: 5px solid #10b981;
            background: linear-gradient(135deg, rgba(16, 185, 129, 0.18), rgba(25, 25, 32, 0.88));
        }
        .toast-notification.toast-error {
            border-left: 5px solid #ef4444;
            background: linear-gradient(135deg, rgba(239, 68, 68, 0.18), rgba(25, 25, 32, 0.88));
        }
        .toast-notification.toast-warning {
            border-left: 5px solid #f59e0b;
            background: linear-gradient(135deg, rgba(245, 158, 11, 0.18), rgba(25, 25, 32, 0.88));
        }
        .toast-notification.toast-info {
            border-left: 5px solid #3b82f6;
            background: linear-gradient(135deg, rgba(59, 130, 246, 0.18), rgba(25, 25, 32, 0.88));
        }
        .toast-icon {
            display: flex;
            align-items: center;
            justify-content: center;
            flex-shrink: 0;
            width: 22px;
            height: 22px;
        }
        .toast-message {
            flex-grow: 1;
            white-space: pre-line;
        }

        /* Premium Overlay Confirm Dialog CSS System */
        .confirm-backdrop {
            position: fixed;
            top: 0;
            left: 0;
            width: 100%;
            height: 100%;
            background: rgba(10, 10, 14, 0.6);
            backdrop-filter: blur(12px);
            -webkit-backdrop-filter: blur(12px);
            z-index: 999998;
            display: flex;
            align-items: center;
            justify-content: center;
            opacity: 0;
            transition: opacity 0.3s cubic-bezier(0.165, 0.84, 0.44, 1);
            pointer-events: none;
        }
        .confirm-backdrop.show {
            opacity: 1;
            pointer-events: auto;
        }
        .confirm-card {
            background: rgba(30, 30, 40, 0.9);
            border: 1px solid rgba(255, 255, 255, 0.15);
            box-shadow: 0 24px 60px rgba(0, 0, 0, 0.5);
            border-radius: 24px;
            width: calc(100% - 32px);
            max-width: 440px;
            padding: 26px;
            color: #ffffff;
            transform: translateY(40px) scale(0.9);
            transition: all 0.35s cubic-bezier(0.165, 0.84, 0.44, 1);
            font-family: -apple-system, BlinkMacSystemFont, "Segoe UI", Roboto, Helvetica, Arial, sans-serif;
        }
        .confirm-backdrop.show .confirm-card {
            transform: translateY(0) scale(1);
        }
        .confirm-header {
            display: flex;
            align-items: center;
            gap: 12px;
            margin-bottom: 16px;
        }
        .confirm-header-icon {
            width: 32px;
            height: 32px;
            border-radius: 10px;
            display: flex;
            align-items: center;
            justify-content: center;
            background: rgba(239, 68, 68, 0.15);
            color: #ef4444;
        }
        .confirm-header-icon.confirm-header-primary {
            background: rgba(59, 130, 246, 0.15);
            color: #3b82f6;
        }
        .confirm-title {
            font-size: 18px;
            font-weight: 700;
            letter-spacing: -0.02em;
        }
        .confirm-message {
            font-size: 14px;
            line-height: 1.6;
            color: #cbd5e1;
            margin-bottom: 24px;
            white-space: pre-line;
        }
        .confirm-buttons {
            display: flex;
            gap: 12px;
            justify-content: flex-end;
        }
        .confirm-btn {
            padding: 12px 22px;
            border-radius: 12px;
            font-size: 14px;
            font-weight: 600;
            cursor: pointer;
            border: none;
            transition: all 0.2s cubic-bezier(0.165, 0.84, 0.44, 1);
            outline: none !important;
        }
        .confirm-btn-cancel {
            background: rgba(255, 255, 255, 0.08);
            color: #e2e8f0;
            border: 1px solid rgba(255, 255, 255, 0.05);
        }
        .confirm-btn-cancel:hover {
            background: rgba(255, 255, 255, 0.14);
        }
        .confirm-btn-action {
            background: linear-gradient(135deg, #ef4444, #dc2626);
            color: #ffffff;
            box-shadow: 0 4px 18px rgba(239, 68, 68, 0.35);
        }
        .confirm-btn-action:hover {
            opacity: 0.95;
            transform: translateY(-1px);
            box-shadow: 0 6px 22px rgba(239, 68, 68, 0.45);
        }
        .confirm-btn-primary {
            background: linear-gradient(135deg, #3b82f6, #2563eb);
            color: #ffffff;
            box-shadow: 0 4px 18px rgba(59, 130, 246, 0.35);
        }
        .confirm-btn-primary:hover {
            opacity: 0.95;
            transform: translateY(-1px);
            box-shadow: 0 6px 22px rgba(59, 130, 246, 0.45);
        }
    `;
    document.head.appendChild(style);

    // 2. Setup DOM Elements
    var toastContainer = document.createElement('div');
    toastContainer.className = 'toast-container';
    document.body.appendChild(toastContainer);

    var confirmBackdrop = document.createElement('div');
    confirmBackdrop.className = 'confirm-backdrop';
    confirmBackdrop.innerHTML = `
        <div class="confirm-card">
            <div class="confirm-header">
                <div class="confirm-header-icon" id="confirmIcon">
                    <svg viewBox="0 0 24 24" width="20" height="20" stroke="currentColor" stroke-width="2.5" fill="none" stroke-linecap="round" stroke-linejoin="round"><path d="M10.29 3.86L1.82 18a2 2 0 0 0 1.71 3h16.94a2 2 0 0 0 1.71-3L13.71 3.86a2 2 0 0 0-3.42 0z"></path><line x1="12" y1="9" x2="12" y2="13"></line><line x1="12" y1="17" x2="12.01" y2="17"></line></svg>
                </div>
                <div class="confirm-title" id="confirmTitle">Confirm Action</div>
            </div>
            <div class="confirm-message" id="confirmMsg">Are you sure you want to proceed?</div>
            <div class="confirm-buttons">
                <button class="confirm-btn confirm-btn-cancel" id="confirmBtnCancel">Cancel</button>
                <button class="confirm-btn" id="confirmBtnOk">Confirm</button>
            </div>
        </div>
    `;
    document.body.appendChild(confirmBackdrop);

    // SVGs for Toast Notifications
    var icons = {
        success: '<svg class="toast-icon" viewBox="0 0 24 24" width="22" height="22" stroke="#10b981" stroke-width="2.5" fill="none" stroke-linecap="round" stroke-linejoin="round"><path d="M22 11.08V12a10 10 0 1 1-5.93-9.14"></path><polyline points="22 4 12 14.01 9 11.01"></polyline></svg>',
        error: '<svg class="toast-icon" viewBox="0 0 24 24" width="22" height="22" stroke="#ef4444" stroke-width="2.5" fill="none" stroke-linecap="round" stroke-linejoin="round"><circle cx="12" cy="12" r="10"></circle><line x1="15" y1="9" x2="9" y2="15"></line><line x1="9" y1="9" x2="15" y2="15"></line></svg>',
        warning: '<svg class="toast-icon" viewBox="0 0 24 24" width="22" height="22" stroke="#f59e0b" stroke-width="2.5" fill="none" stroke-linecap="round" stroke-linejoin="round"><path d="M10.29 3.86L1.82 18a2 2 0 0 0 1.71 3h16.94a2 2 0 0 0 1.71-3L13.71 3.86a2 2 0 0 0-3.42 0z"></path><line x1="12" y1="9" x2="12" y2="13"></line><line x1="12" y1="17" x2="12.01" y2="17"></line></svg>',
        info: '<svg class="toast-icon" viewBox="0 0 24 24" width="22" height="22" stroke="#3b82f6" stroke-width="2.5" fill="none" stroke-linecap="round" stroke-linejoin="round"><circle cx="12" cy="12" r="10"></circle><line x1="12" y1="16" x2="12" y2="12"></line><line x1="12" y1="8" x2="12.01" y2="8"></line></svg>'
    };

    // 3. Implement Custom Toast Alert system
    window.showToast = function(message, type) {
        if (!type) {
            // Smart auto-detection of type based on keyword analysis
            var msgLower = message.toLowerCase();
            if (msgLower.indexOf('success') !== -1 || msgLower.indexOf('successful') !== -1 || msgLower.indexOf('complete') !== -1 || msgLower.indexOf('good') !== -1) {
                type = 'success';
            } else if (msgLower.indexOf('fail') !== -1 || msgLower.indexOf('error') !== -1 || msgLower.indexOf('warning') !== -1 || msgLower.indexOf('missing') !== -1 || msgLower.indexOf('incomplete') !== -1) {
                type = 'error';
            } else {
                type = 'info';
            }
        }

        var toast = document.createElement('div');
        toast.className = 'toast-notification toast-' + type;
        toast.innerHTML = (icons[type] || icons.info) + '<div class="toast-message">' + message + '</div>';

        toastContainer.appendChild(toast);

        // Force a layout reflow for CSS transitions to work
        toast.offsetHeight;

        toast.classList.add('show');

        // Dismiss Toast on click
        toast.addEventListener('click', function() {
            toast.classList.remove('show');
            setTimeout(function() {
                if (toast.parentNode) {
                    toast.parentNode.removeChild(toast);
                }
            }, 400);
        });

        // Auto dismiss after 5 seconds
        setTimeout(function() {
            if (toast.classList.contains('show')) {
                toast.classList.remove('show');
                setTimeout(function() {
                    if (toast.parentNode) {
                        toast.parentNode.removeChild(toast);
                    }
                }, 400);
            }
        }, 5000);
    };

    // Override system alert globally
    window.alert = function(msg) {
        window.showToast(msg);
    };

    // 4. Implement Custom Confirmation Modal System
    var confirmCallback = null;

    window.customConfirm = function(message, callback, isPrimary) {
        confirmCallback = callback;

        // Customise Confirm Icon & Style
        var iconEl = document.getElementById('confirmIcon');
        var titleEl = document.getElementById('confirmTitle');
        var okBtn = document.getElementById('confirmBtnOk');

        if (isPrimary) {
            iconEl.className = "confirm-header-icon confirm-header-primary";
            iconEl.innerHTML = '<svg viewBox="0 0 24 24" width="20" height="20" stroke="currentColor" stroke-width="2.5" fill="none" stroke-linecap="round" stroke-linejoin="round"><circle cx="12" cy="12" r="10"></circle><line x1="12" y1="16" x2="12" y2="12"></line><line x1="12" y1="8" x2="12.01" y2="8"></line></svg>';
            titleEl.textContent = "Please Confirm";
            okBtn.className = "confirm-btn confirm-btn-primary";
        } else {
            iconEl.className = "confirm-header-icon";
            iconEl.innerHTML = '<svg viewBox="0 0 24 24" width="20" height="20" stroke="currentColor" stroke-width="2.5" fill="none" stroke-linecap="round" stroke-linejoin="round"><path d="M10.29 3.86L1.82 18a2 2 0 0 0 1.71 3h16.94a2 2 0 0 0 1.71-3L13.71 3.86a2 2 0 0 0-3.42 0z"></path><line x1="12" y1="9" x2="12" y2="13"></line><line x1="12" y1="17" x2="12.01" y2="17"></line></svg>';
            titleEl.textContent = "Warning Required";
            okBtn.className = "confirm-btn confirm-btn-action";
        }

        document.getElementById('confirmMsg').textContent = message;
        confirmBackdrop.classList.add('show');
    };

    // Close Confirmation helper
    function closeConfirm() {
        confirmBackdrop.classList.remove('show');
    }

    document.getElementById('confirmBtnCancel').addEventListener('click', function() {
        closeConfirm();
    });

    document.getElementById('confirmBtnOk').addEventListener('click', function() {
        closeConfirm();
        if (typeof confirmCallback === 'function') {
            confirmCallback();
        }
    });

    // Close on backdrop click (cancel)
    confirmBackdrop.addEventListener('click', function(e) {
        if (e.target === confirmBackdrop) {
            closeConfirm();
        }
    });

})();
