import Long from 'long';

export interface GrpcTimestamp {
  seconds: Long | number;
  nanos: number;
}

export function timestampToDate(ts: GrpcTimestamp): Date {
  const seconds =
    typeof ts.seconds === 'number' ? ts.seconds : ts.seconds.toNumber();

  return new Date(seconds * 1000 + ts.nanos / 1e6);
}
