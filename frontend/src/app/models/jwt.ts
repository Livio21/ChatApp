export interface Jwt {
  token: string;
  username: string;
  /** Jackson may serialize java.util.Date as epoch ms or ISO string */
  expiration: string ;
}
