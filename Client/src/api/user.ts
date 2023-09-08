import axios from '../utility/axios';
import { StatusCode } from './category';

export interface Status {
  statId: StatusCode;
  statLevel: number;
  statExp: number;
  requiredExp: number;
}

export interface UserInfo {
  id: number;
  email: string;
  nickname: string;
  profileImage: string;
  attendance: boolean;
  statuses: Status[];
}

export const getUserInfo = async (): Promise<UserInfo> => {
  const res = await axios.get('user/mypage');
  return res.data;
};

export const changePassword = async (password: string): Promise<string> => {
  const res = await axios.patch('user/mypage/edit/password', {
    password,
  });
  return res.data;
};
