import { useState } from 'react';
import Image from 'next/image';
import { Avatar } from '@mui/material';
import { Chat } from './ChatList';
import SwipeableDrawer from '@mui/material/SwipeableDrawer';

interface ChatProp {
  chat: Chat;
}

function ChatListItem({ chat }: ChatProp) {
  const userImagePath =
    'https://i.namu.wiki/i/xss2U6BFSuoMjDMssQDkkUNNvzOgpWjkTJ_pgdcRF034Qc_vlAZ6yOVI6ik1rhHBWpxovuBg5MIE55Wcf54uyLI6KplwA5lrYS5Omaa-G1MXvAawlW_QQO0gCR63K_TdrlqX75TyqynnSF89211hqg.webp';
  const myName = '김태학';

  // 날짜 변환
  const timeForToday = (value: string) => {
    const today = new Date();
    const timeValue = new Date(value);
    const betweenTime = Math.floor((today.getTime() - timeValue.getTime()) / 1000 / 60);
    if (betweenTime < 1) return '방금전';
    if (betweenTime < 60) {
      return `${betweenTime}분전`;
    }
    const betweenTimeHour = Math.floor(betweenTime / 60);
    if (betweenTimeHour < 24) {
      return `${betweenTimeHour}시간전`;
    }
    const betweenTimeDay = Math.floor(betweenTimeHour / 24);
    if (betweenTimeDay < 30) {
      return `${betweenTimeDay}일전`;
    }
    const betweenTimeMonth = Math.floor(betweenTimeDay / 30);
    if (betweenTimeMonth < 12) {
      return `${betweenTimeMonth}달전`;
    }
    return `${Math.floor(betweenTimeDay / 365)}년전`;
  };

  const [commentModal, setCommentModal] = useState<any>(false);
  const [modalState, setModalState] = useState<string>('init');

  const openModal = () => {
    setCommentModal(true);
  };
  const closeModal = () => {
    setCommentModal(false);
    setModalState('init');
  };

  const changeModalState = (e: string) => {
    setModalState(e);
  };
  const deleteChat = () => {
    alert('삭제');
  };
  const modifyChat = () => {
    alert('수정');
  };
  const reportChat = () => {
    alert('신고');
  };

  const popupDelete = (
    <div className="flex justify-center">
      <div className="my-6 flex w-2/3 flex-col items-center justify-center">
        <p className="text-center text-2xl text-PrimaryLight">이 댓글을 정말 삭제하시겠습니까?</p>
        <hr className="my-2 w-full border border-PrimaryLight bg-PrimaryLight" />
        <button
          className="flex w-1/2 flex-col items-center justify-center py-3"
          onClick={deleteChat}
        >
          <p className="text-center">삭제하기</p>
        </button>
      </div>
    </div>
  );

  const popupModify = (
    <div className="flex justify-center">
      <div className="my-6 flex w-2/3 flex-col items-center justify-center">
        <p className="text-center text-2xl text-PrimaryLight">이 댓글을 정말 수정하시겠습니까?</p>
        <hr className="my-2 w-full border border-PrimaryLight bg-PrimaryLight" />
        <button
          className="flex w-1/2 flex-col items-center justify-center py-3"
          onClick={modifyChat}
        >
          <p className="text-center">수정하기 div</p>
        </button>
      </div>
    </div>
  );

  const popupReport = (
    <div className="flex justify-center">
      <div className="my-6 flex w-2/3 flex-col items-center justify-center">
        <p className="text-center text-2xl text-PrimaryLight">이 댓글을 정말 신고하시겠습니까?</p>
        <hr className="my-2 w-full border border-PrimaryLight bg-PrimaryLight" />
        <button
          className="flex w-1/2 flex-col items-center justify-center py-3"
          onClick={reportChat}
        >
          <p className="text-center">신고하기</p>
        </button>
      </div>
    </div>
  );

  const popupListForUser = (
    <div className="flex justify-center">
      <div className="my-6 flex w-2/3 flex-col items-center justify-center">
        <p className="text-center text-2xl text-PrimaryLight">댓글 설정</p>
        <hr className="my-2 w-full border border-PrimaryLight bg-PrimaryLight" />
        <button
          className="flex w-1/2 flex-col items-center justify-center py-3"
          onClick={() => {
            changeModalState('del');
          }}
        >
          <p className="text-center">삭제하기</p>
        </button>
        <button
          className="flex w-1/2 flex-col items-center justify-center py-3"
          onClick={() => {
            changeModalState('modify');
          }}
        >
          <p className="text-center">수정하기</p>
        </button>
      </div>
    </div>
  );

  const popupListForElse = (
    <div className="flex justify-center">
      <div className="my-6 flex w-2/3 flex-col items-center justify-center">
        <p className="text-center text-2xl text-PrimaryLight">댓글 설정</p>
        <hr className="my-2 w-full border border-PrimaryLight bg-PrimaryLight" />
        <button
          className="flex w-1/2 flex-col items-center justify-center py-3"
          onClick={() => {
            changeModalState('report');
          }}
        >
          <p className="text-center">신고하기</p>
        </button>
      </div>
    </div>
  );

  const popupForUser = () => {
    if (modalState === 'init') {
      return popupListForUser;
    } else if (modalState === 'del') {
      return popupDelete;
    } else if (modalState === 'modify') {
      return popupModify;
    }
  };
  const popupForElse = () => {
    if (modalState === 'init') {
      return popupListForElse;
    } else if (modalState === 'report') {
      return popupReport;
    }
  };

  const popupDiv = (
    <SwipeableDrawer anchor={'bottom'} open={commentModal} onOpen={openModal} onClose={closeModal}>
      {chat.user.nickname === myName ? popupForUser() : popupForElse()}
    </SwipeableDrawer>
  );

  if (chat.user.nickname === myName) {
    return (
      <div className="my-2 ml-auto flex max-w-[70%] items-center">
        {popupDiv}
        <div>
          <div className="mr-1 flex max-w-full rounded bg-BackgroundLightComponent p-1.5">
            <p>{chat.content}</p>
            <div>
              <button
                className="mt-1"
                onClick={() => {
                  openModal();
                  console.log('더보기클릭함');
                }}
              >
                <Image src="/images/More.png" width={20} height={20} alt="#" />
              </button>
            </div>
          </div>
          <div>
            <p className="text-right">
              {timeForToday(chat.createTime)}|{chat.user.nickname}
            </p>
          </div>
        </div>
        <Avatar alt="Remy Sharp" src={userImagePath} />
      </div>
    );
  } else {
    return (
      <div className="my-2 flex max-w-[70%] items-center">
        {popupDiv}
        <Avatar alt="Remy Sharp" src={userImagePath} />
        <div>
          <div className="ml-1 flex max-w-full rounded bg-BackgroundLightComponent p-1.5">
            <p>{chat.content}</p>
            <div>
              <button
                className="mt-1"
                onClick={() => {
                  openModal();
                  console.log('더보기클릭함');
                }}
              >
                <Image src="/images/More.png" width={20} height={20} alt="#" />
              </button>
            </div>
          </div>
          <div>
            <p>
              {chat.user.nickname}|{timeForToday(chat.createTime)}
            </p>
          </div>
        </div>
      </div>
    );
  }
}

export default ChatListItem;
