const toggleButton = document.getElementById('toggleTerms');
const termsContent = document.getElementById('termsContent');

toggleButton.addEventListener('click', function () {
    termsContent.classList.toggle('collapsed');

    if (termsContent.classList.contains('collapsed')) {
        toggleButton.textContent = '전체 약관 보기 ▼';
    } else {
        toggleButton.textContent = '약관 접기 ▲';
    }
});

const togglePrivacyButton = document.getElementById('togglePrivacy');
const privacyContent = document.getElementById('privacyContent');

togglePrivacyButton.addEventListener('click', function () {
    privacyContent.classList.toggle('collapsed');

    if (privacyContent.classList.contains('collapsed')) {
        togglePrivacyButton.textContent = '전체 내용 보기 ▼';
    } else {
        togglePrivacyButton.textContent = '내용 접기 ▲';
    }
});

const agreeTermsCheckbox = document.getElementById('agreeTerms');
const agreePrivacyCheckbox = document.getElementById('agreePrivacy');
const agreeButton = document.getElementById('agreeButton');

function checkAllAgreed() {
    if (agreeTermsCheckbox.checked && agreePrivacyCheckbox.checked) {
        agreeButton.disabled = false;
    } else {
        agreeButton.disabled = true;
    }
}

agreeTermsCheckbox.addEventListener('change', checkAllAgreed);
agreePrivacyCheckbox.addEventListener('change', checkAllAgreed);

agreeButton.addEventListener('click', function () {
    //alert('약관에 동의하셨습니다. 회원가입 페이지로 이동합니다.');
    window.location.replace("http://localhost:3000/signup");
});