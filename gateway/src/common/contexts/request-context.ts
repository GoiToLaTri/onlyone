import { AsyncLocalStorage } from 'async_hooks';

export type RequestStore = {
  token?: string;
};

export const requestContext = new AsyncLocalStorage<RequestStore>();
