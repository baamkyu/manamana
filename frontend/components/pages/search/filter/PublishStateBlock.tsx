import { useState } from "react";

interface Data {
    id: number,
    status: string
}

interface Props {
    value: Data;
    status: boolean;
    selectBlock: (data :Data) => void;
    unSelectBlock: (data: Data) => void;
}

export default function PublishStateBlock(props: Props) {
    const [selectStatus, setSelectStatus] = useState<boolean>(props.status);
    /*
    * @Method
    * 버튼을 클릭했을 때 실행되는 메소드
    *  */
    const onButtonClick = () => {
        if(!selectStatus){
            props.selectBlock({id: props.value.id, status: props.value.status});
            setSelectStatus(true);
        }
        else{
            props.unSelectBlock({id: props.value.id, status: props.value.status});
            setSelectStatus(false);
        }
    }

    return (
        <div className="inline-block w-full">
            {
                selectStatus ?
                    <button className="w-full h-12 rounded-md bg-SecondaryLight text-FontPrimaryDark text-center" onClick={onButtonClick}>
                        {props.value.status}
                    </button>
                    :
                    <button className="w-full h-12 rounded-md bg-BackgroundLightComponent text-FontPrimaryLight text-center" onClick={onButtonClick}>
                        {props.value.status}
                    </button>
            }
        </div>
    )
}