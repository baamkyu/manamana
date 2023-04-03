import WebtoonBreakLabel from './WebtoonBreakLabel';
import WebtoonCompleteLabel from './WebtoonCompleteLabel';
import { useRouter } from 'next/router';

type Props = {
  imageUrl: string;
  webtoonName: string;
  status: string;
  id: number;
};

export default function WebtoonItem({ id, imageUrl, webtoonName, status }: Props) {
  const router = useRouter();

  const onWebtoonClick = () => {
    if (id > 0) {
      router.push(`/detail/${id}`);
    }
  };

  return (
    <div className="inline-block" onClick={onWebtoonClick}>
      <div className="mr-2 inline-block flex flex-col">
        <img src={imageUrl} alt="imageURL" className="h-30 mt-2 w-24"></img>
        <div className="mt-1 flex h-4 flex-row items-center overflow-hidden text-sm">
          {status === '휴재중' ? <WebtoonBreakLabel /> : <></>}
          {status === '완결' ? <WebtoonCompleteLabel /> : <></>}
          <div className="text-semibold flex items-center justify-center text-[16px]">
            {/* 웹툰제목 길이가 길면 6글자만 출력 */}
            {/* 휴재/완결 버튼 있고 웹툰제목 길이가 길면 4글자만 출력 */}
            {(status === '휴재중' || status === '완결') && webtoonName.length > 4
              ? webtoonName.substring(0, 4).concat('...')
              : webtoonName.length > 6
              ? webtoonName.substring(0, 6).concat('...')
              : webtoonName}
          </div>
        </div>
      </div>
    </div>
  );
}
