import { useCallback, useEffect, useState } from 'react'
import { useLocation, useNavigate } from 'react-router-dom'
import { NavigateType } from '@src/types/GlobalType'
// import LogoImg from '@src/assets/logo.svg'
// import SlimLogoImg from '@src/assets/logo_slim.svg'
// import LogoImg from '@src/assets/logo.svg'
// import BlueLogoImg from '@src/assets/logo_blue.svg'
import NavyLogoImg from '@src/assets/logo_navy.svg'
import WhiteLogo from '@src/assets/logo_white.svg'
import ChatImg from '@src/assets/msg_black.svg'
import BlueChatImg from '@src/assets/msg_navy.svg'
import WhiteChatImg from '@src/assets/msg_white.svg'
import styled from 'styled-components'
import HeaderDropdown from '@src/common/HeaderDropdown'
import useCommunityStore from '@src/stores/communityStore'
import useSelectPlaceStore from '@src/stores/selectPlaceStore'

import three_line from '@src/assets/three_line.svg'
import three_line_gray from '@src/assets/three_line_gray.svg'
import analysisStore from '@src/stores/analysisStore'
import { Avatar } from '@mui/joy'
import RealTimeSearchTerms from '@src/containers/header/RealTimeSearchTerms'
import useWindowWidth from '@src/hooks/useWindowWidth'

const Container = styled.header<{ $isTransparent: boolean; $isMain: boolean }>`
  height: 70px;
  display: flex;
  align-items: center;
  justify-content: space-between;
  border-bottom: ${props => (props.$isMain ? '' : '#c4c4c4 1px solid')};
  // 상단 고정하기 위한 코드
  background-color: ${props => (props.$isMain ? 'transparent' : '#fff')};
  //background-color: #ffffff;
  z-index: 100;
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  padding-inline: 3vw;
  width: calc(100vw);
  opacity: ${props => (props.$isTransparent ? 1 : 0)};
  pointer-events: ${props => (props.$isTransparent ? 'auto' : 'none')};
  transition:
    opacity 0.3s,
    background-color 0.3s;

  @media (max-width: 1015px) {
    opacity: 1;
    background-color: #ffffff;
    pointer-events: auto;
  }
`

const MenuListLeft = styled.div<{ isMenuOpen?: boolean }>`
  display: flex;

  @media (max-width: 1015px) {
    flex-direction: column;
    display: ${({ isMenuOpen }) => (isMenuOpen ? 'flex' : 'none')};
  }
`

const MenuListRight = styled.div<{ isMenuOpen?: boolean }>`
  width: auto;
  display: flex;
  justify-content: right;
  //margin: 0 0.5rem;

  @media (max-width: 1015px) {
    flex-direction: column;
    display: ${({ isMenuOpen }) => (isMenuOpen ? 'flex' : 'none')};
  }
`

const Menu = styled.div<{
  $isActive?: boolean
  $isMain?: boolean
  $atTop?: boolean
}>`
  height: 66px;
  padding: 0 10px;
  font-size: 16px;
  display: flex;
  align-items: center;
  cursor: pointer;
  font-weight: bold;
  border-bottom: 2px solid
    ${props => {
      if (props.$isActive) {
        return props.$isMain && props.$atTop ? 'white' : '#1549B5'
      }
      return 'none'
    }};
  color: ${props => {
    if (props.$isActive)
      return props.$isMain && props.$atTop ? 'white' : '#1549B5'
    return props.$isMain && props.$atTop ? 'white' : 'black'
  }};

  &:hover {
    color: ${props => (props.$isMain && props.$atTop ? 'white' : '#1549B5')};
    border-bottom: 2px solid
      ${props => (props.$isMain && props.$atTop ? 'white' : '#1549B5')};
  }

  @media (max-width: 1200px) {
    font-size: 14px;
    padding: 0 10px;
  }
`

const LogoDiv = styled.div``

const Icon = styled.img``

const Logo = styled.img`
  scale: 1;
  margin: 7px 20px 0 0;
  cursor: pointer;
`

const HamburgerMenu = styled.div`
  font-size: 60px;
  display: none;
  position: absolute;
  right: 0;
  height: 68px;

  @media (max-width: 1015px) {
    display: flex; // 화면 너비가 1200px 이하일 경우 햄버거 메뉴 표시

    align-items: center;
    justify-content: center;
    cursor: pointer;
    margin-right: 3vw;
  }
`
const DropdownMenu = styled.div`
  position: relative;
  top: 0;
  right: 0;

  margin-top: 170px;
  border-radius: 20px;
  width: 150px;
  height: 100px;
`

const BlankDiv = styled.div`
  flex-grow: 1;
`

// 실시간 검색창
const RealTimeSearchBar = styled.div<{
  $isMain?: boolean
  $atTop?: boolean
  $isWide?: boolean
}>`
  height: 68px;
  display: flex;
  flex-direction: column;
  justify-content: center;
  align-items: start;
  color: ${props =>
    props.$isMain && props.$atTop && props.$isWide ? 'white' : ''};

  @media only screen and (max-width: 1015px) {
    display: none;
  }
`

const RealTimeTitle = styled.div`
  font-weight: 600;
  font-size: 0.8rem;
  padding: 0 0 0 15px;
`

const HeaderContainer = () => {
  const [activeMenu, setActiveMenu] = useState<string | null>(null)
  const navigate = useNavigate()
  const location = useLocation()
  const [menuOpen, setMenuOpen] = useState<boolean>(false)

  // 스크롤 내렸을 때 사라지게 하는 로직
  const [isTransparent, setIsTransparent] = useState<boolean>(true)
  const [atTop, setAtTop] = useState<boolean>(true)
  const [lastScrollY, setLastScrollY] = useState<number>(0)
  // 프로필 이미지 저장
  const [profileImg, setProfileImg] = useState<string | undefined>(undefined)

  // 호버시 적용

  const [isHovered, setIsHovered] = useState(false)
  const [isClicked, setIsClicked] = useState(false)

  const isWide = useWindowWidth()
  // 현재 스크롤과 이전 스크롤 상태 비교해서 올림, 내림 스크롤 판단하는 로직
  const handleScroll = useCallback(() => {
    const currentScrollY = window.scrollY
    // 맨 위인지?
    setAtTop(currentScrollY === 0)
    if (currentScrollY > lastScrollY) {
      setIsTransparent(false)
    } else {
      setIsTransparent(true)
    }
    setLastScrollY(currentScrollY)
  }, [lastScrollY])

  useEffect(() => {
    window.addEventListener('scroll', handleScroll)
    return () => {
      window.removeEventListener('scroll', handleScroll)
    }
  }, [handleScroll, lastScrollY])

  // 상권 분석이나 상권 추천 페이지 이동 시 드롭다운 데이터 초기화 하기 위한 store
  const { setSelectedGoo, setSelectedDong, setSelectedCommercial } =
    useSelectPlaceStore(state => ({
      setSelectedGoo: state.setSelectedGoo,
      setSelectedDong: state.setSelectedDong,
      setSelectedCommercial: state.setSelectedCommercial,
    }))

  // 상권 분석 페이지 이동 시 드롭다운 데이터 초기화 하기 위한 store
  const { setSelectedServiceType } = analysisStore(state => ({
    setSelectedServiceType: state.setSelectedServiceType,
  }))

  // 메인 페이지에서만 투명한 배경 설정
  useEffect(() => {
    if (
      location.pathname === '/analysis' ||
      location.pathname === '/recommend'
    ) {
      setSelectedGoo({ name: '자치구', code: 0 })
      setSelectedDong({ name: '행정동', code: 0 })
      setSelectedCommercial({ name: '상권', code: 0 })
      setSelectedServiceType('')
    }
  }, [
    location.pathname,
    setSelectedCommercial,
    setSelectedDong,
    setSelectedGoo,
    setSelectedServiceType,
  ])

  // 로그인 상태 확인 (localStorage 사용)
  const userLoggedIn = localStorage.getItem('isLogIn') === 'true'

  // 프로필 이미지 가져오기
  useEffect(() => {
    if (userLoggedIn) {
      const memberInfo = localStorage.getItem('memberInfo')
      if (memberInfo) {
        const { profileImage } = JSON.parse(memberInfo)
        setProfileImg(profileImage)
      }
    }
  }, [userLoggedIn])

  const LocationData = [
    {
      name: '구별현황',
      location: '/status',
    },
    {
      name: '상권분석',
      location: '/analysis',
    },
    {
      name: '상권추천',
      location: '/recommend',
    },
    // {
    //   name: '창업시뮬레이션',
    //   location: '/simulation',
    // },
    {
      name: '커뮤니티',
      location: '/community/list',
    },
    {
      name: '채팅',
      location: '/chatting/list',
    },
    {
      name: '프로필',
      location: '/profile/bookmarks/analysis',
    },
    {
      name: '로그인',
      location: '/login',
    },
    {
      name: '회원가입',
      location: '/register',
    },
  ]

  const { setSelectedCategory } = useCommunityStore(state => ({
    setSelectedCategory: state.setSelectedCategory,
  }))

  // 경로에 따라 activeMenu 설정
  /* eslint-disable react-hooks/exhaustive-deps */
  useEffect(() => {
    const activeItem = LocationData.find(
      item => item.location === location.pathname,
    )
    setActiveMenu(activeItem ? activeItem.name : null)
  }, [location.pathname])

  const handleMenuClick = (menuName: string) => {
    const menuItem = LocationData.find(item => item.name === menuName)
    if (menuItem) {
      navigate(menuItem.location)
      // 커뮤니티를 클릭했을 때 selectedCategory 설정
      if (menuName === '커뮤니티') {
        setSelectedCategory({
          name: '전체보기',
          value: '',
          iconActive: three_line,
          iconInactive: three_line_gray,
        })
      }
    }
    setActiveMenu(menuName)
  }

  const goNavigate = ({ url }: NavigateType) => {
    navigate(url)
  }

  let iconSrc
  if (location.pathname === '/' && atTop) {
    iconSrc = WhiteChatImg
  } else if (location.pathname === '/chatting/list') {
    iconSrc = BlueChatImg
  } else {
    iconSrc = ChatImg
  }

  return (
    <Container
      $isTransparent={isTransparent}
      $isMain={location.pathname === '/'}
    >
      <LogoDiv onClick={() => goNavigate({ url: '/' })}>
        {/* <Logo src={SlimLogoImg} alt="logo" /> */}
        {isWide && location.pathname === '/' && atTop ? (
          <Logo src={WhiteLogo} alt="logo" />
        ) : (
          <Logo src={NavyLogoImg} alt="logo" />
        )}
      </LogoDiv>

      <MenuListLeft>
        {['구별현황', '상권분석', '상권추천', '커뮤니티'].map(menuName => (
          <Menu
            key={menuName}
            $isActive={activeMenu === menuName}
            $isMain={location.pathname === '/'}
            $atTop={atTop}
            onClick={() => handleMenuClick(menuName)}
          >
            {menuName}
          </Menu>
        ))}
      </MenuListLeft>

      <BlankDiv />
      {/* <BlankDiv /> */}
      {/* 실시간 인기 검색어 */}
      <RealTimeSearchBar
        $isMain={location.pathname === '/'}
        $atTop={atTop}
        $isWide={isWide}
        onClick={() => {
          setIsClicked(true)
        }}
        onMouseEnter={() => {
          setIsHovered(true)
        }}
        onMouseLeave={() => {
          setIsHovered(false)
          setIsClicked(false)
        }}
      >
        <RealTimeTitle>실시간 인기 검색어</RealTimeTitle>

        <RealTimeSearchTerms isHovered={isHovered} isClicked={isClicked} />
      </RealTimeSearchBar>

      {isWide ? (
        <MenuListRight>
          <Menu
            $isActive={activeMenu === '채팅'}
            $isMain={location.pathname === '/'}
            $atTop={atTop}
            onClick={() => {
              handleMenuClick('채팅')
              window.location.reload()
            }}
          >
            <Icon src={iconSrc} />
          </Menu>
          {userLoggedIn ? (
            <Menu
              $isActive={activeMenu === '프로필'}
              $isMain={location.pathname === '/'}
              $atTop={atTop}
              onClick={() => handleMenuClick('프로필')}
            >
              <Avatar src={profileImg} sx={{ width: 35, height: 35 }} />
            </Menu>
          ) : (
            ['로그인', '회원가입'].map(menuName => (
              <Menu
                key={menuName}
                $isActive={activeMenu === menuName}
                $isMain={location.pathname === '/'}
                $atTop={atTop}
                onClick={() => handleMenuClick(menuName)}
              >
                {menuName}
              </Menu>
            ))
          )}
        </MenuListRight>
      ) : (
        <HamburgerMenu onClick={() => setMenuOpen(!menuOpen)}>≡</HamburgerMenu>
      )}

      {menuOpen && (
        <DropdownMenu>
          <HeaderDropdown
            menuData={LocationData.filter(item =>
              userLoggedIn
                ? item.name !== '로그인' && item.name !== '회원가입'
                : item.name !== '프로필',
            )}
            isMenuOpen={menuOpen}
            setMenuOpen={setMenuOpen}
          />
        </DropdownMenu>
      )}
    </Container>
  )
}
export default HeaderContainer
