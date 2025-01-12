import { CookiesProvider } from 'react-cookie'
import { Route, Routes, useLocation } from 'react-router-dom'
import GlobalStyles from '@src/GlobalStyles.tsx'
import MainPage from '@src/pages/MainPage'
import SignUpPage from '@src/pages/SignUpPage'
import SignUpGeneralPage from '@src/pages/SignUpGeneralPage'
import LoginPage from '@src/pages/LoginPage'
import SocialLoadingPage from '@src/pages/SocialLoadingPage'
import ProfilePage from '@src/pages/ProfilePage'
import BookmarksPage from '@src/pages/BookmarksPage'
import BookmarksListPage from '@src/pages/BookmarksListPage'
import AnalysisBookmarksPage from '@src/pages/AnalysisBookmarksPage'
import RecommendBookmarksPage from '@src/pages/RecommendBookmarksPage'
import SimulationBookmarksPage from '@src/pages/SimulationBookmarksPage'
import SettingsPage from '@src/pages/SettingsPage'
import EditProfilePage from '@src/pages/EditProfilePage'
import ChangePasswordPage from '@src/pages/ChangePasswordPage'
import WithdrawPage from '@src/pages/WithdrawPage'
import AccountDeletedPage from '@src/pages/AccountDeletedPage'
import CommunityPage from '@src/pages/CommunityPage'
import CommunityRegisterPage from '@src/pages/CommunityRegisterPage'
import CommunityDetailPage from '@src/pages/CommunityDetailPage'
import StatusPage from '@src/pages/StatusPage'
import AnalysisPage from '@src/pages/AnalysisPage'
import AnalysisResultPage from '@src/pages/AnalysisResultPage'
import RecommendPage from '@src/pages/RecommendPage'
import SimulationPage from '@src/pages/SimulationPage'
import SimulationReportPage from '@src/pages/SimulationReportPage'
import SimulationReportComparePage from '@src/pages/SimulationReportComparePage'
import ChattingPage from '@src/pages/ChattingPage'
import CommunityListPage from '@src/pages/CommunityListPage'
import { getCookie } from '@src/stores/userStore'
import { useEffect } from 'react'
import ChattingListPage from '@src/pages/ChattingListPage'
import ChattingDetailPage from '@src/pages/ChattingDetailPage'
import ReportKakaoSharePage from '@src/pages/ReportKakaoSharePage'
import SweetAlert2 from '@src/SweetAlert2'
import JSConfetti from 'js-confetti'
import './index.css'

// firebase config 파일 실행
import '@src/util/auth/firebaseMessage'

// 헤더 여부 설정하는 파일
import ConditionalHeader from '@src/util/ConditionalHeader'

declare global {
  interface Window {
    Kakao: any
  }
}

// 축하 이벤트 시 사용할 confetti
export const confetti = new JSConfetti()

function App() {
  const location = useLocation(); // 현재 URL 위치 가져오기
  
  function setScreenSize() {
    const vh = window.innerHeight * 0.01
    document.documentElement.style.setProperty('--vh', `${vh}px`)
  }

  useEffect(() => {
    setScreenSize()
    window.addEventListener('resize', setScreenSize)
    return () => window.removeEventListener('resize', setScreenSize)
  }, []);

  useEffect(() => {
    const accessToken = getCookie('accessToken')
    if (!accessToken) {
      // 로컬스토리지의 memberInfo 삭제
      localStorage.removeItem('memberInfo')
      localStorage.removeItem('isLogIn')
    } 
  }, [location.pathname]) // location.pathname이 변경될 때마다 실행

  return (
    <CookiesProvider>
      <GlobalStyles />
      <SweetAlert2 />
      <ConditionalHeader />
      <Routes>
        <Route path="/" element={<MainPage />} />
        {/* 회원 */}
        <Route path="/register" element={<SignUpPage />} />
        <Route path="/register/general" element={<SignUpGeneralPage />} />
        <Route path="/login" element={<LoginPage />} />
        <Route
          path="/member/loading/:provider"
          element={<SocialLoadingPage />}
        />
        <Route path="/profile/*" element={<ProfilePage />}>
          <Route path="bookmarks" element={<BookmarksPage />}>
            <Route path="" element={<BookmarksListPage />} />
            <Route path="analysis" element={<AnalysisBookmarksPage />} />
            <Route path="recommend" element={<RecommendBookmarksPage />} />
            <Route path="simulation" element={<SimulationBookmarksPage />} />
          </Route>
          <Route path="settings/*" element={<SettingsPage />}>
            <Route path="edit" element={<EditProfilePage />} />
            <Route path="change-password" element={<ChangePasswordPage />} />
            <Route path="withdraw" element={<WithdrawPage />} />
          </Route>
        </Route>
        <Route path="/account-deleted" element={<AccountDeletedPage />} />
        {/* 상권 */}
        <Route path="/status" element={<StatusPage />} />
        <Route path="/analysis" element={<AnalysisPage />}>
          <Route path="result" element={<AnalysisResultPage />} />
          <Route path="simulation" element={<SimulationPage />} />
          <Route path="simulation/report" element={<SimulationReportPage />} />
          <Route
            path="simulation/compare"
            element={<SimulationReportComparePage />}
          />
        </Route>
        <Route path="/recommend" element={<RecommendPage />} />
        <Route path="/simulation" element={<SimulationPage />} />
        <Route path="/simulation/report" element={<SimulationReportPage />} />
        <Route
          path="/simulation/compare"
          element={<SimulationReportComparePage />}
        />
        {/* 커뮤니티 */}
        <Route path="/community/*" element={<CommunityPage />}>
          <Route path="list" element={<CommunityListPage />} />
          <Route path="register" element={<CommunityRegisterPage />} />
          <Route path=":communityId" element={<CommunityDetailPage />} />
        </Route>
        <Route path="/chatting/*" element={<ChattingPage />}>
          <Route path="list" element={<ChattingListPage />} />
          <Route path=":roomId" element={<ChattingDetailPage />} />
        </Route>
        {/*  시뮬레이션 카카오 공유 페이지 */}
        <Route path="/share/:token" element={<ReportKakaoSharePage />} />
      </Routes>
    </CookiesProvider>
  )
}

export default App
