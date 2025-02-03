import { matchPath, useLocation } from 'react-router-dom'
import Header from '@src/containers/header/HeaderContainer'
import Footer from '@src/containers/footer/FooterContainer'

// 헤더와 푸터를 사용하지 않는 페이지 예외 적용
const HeaderAndFooter = ({ content }: { content: string }) => {
  const location = useLocation()
  const currentPathname = location.pathname

  if (
    matchPath('/login', currentPathname) ||
    matchPath('/register', currentPathname) ||
    matchPath('/register/general', currentPathname) ||
    matchPath('/account-deleted', currentPathname)
  ) {
    return null
  }
  return content === 'header' ? <Header /> : <Footer />
}

export default HeaderAndFooter
