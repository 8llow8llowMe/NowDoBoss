import React from 'react'
import ReactDOM from 'react-dom/client'
import { QueryClient, QueryClientProvider } from '@tanstack/react-query'
import { BrowserRouter } from 'react-router-dom' // 전체 애플리케이션의 라우팅 컨텍스트를 최상위에서 관리하기 위함
import App from './App'
// import './index.css'

const queryClient = new QueryClient({
  defaultOptions: {
    queries: {
      refetchOnWindowFocus: false, // 탭 이동 시 쿼리 호출 방지
      refetchOnReconnect: false, // 네트워크 재연결 시 자동 호출 방지 (필요에 따라 설정)
      staleTime: 1000 * 60 * 5, // 5분 동안 데이터를 만료되지 않은 상태로 유지
    },
  },
})

ReactDOM.createRoot(document.getElementById('root')!).render(
  <React.StrictMode>
    <QueryClientProvider client={queryClient}>
      <BrowserRouter>
        <App />
      </BrowserRouter>
    </QueryClientProvider>
  </React.StrictMode>,
)
