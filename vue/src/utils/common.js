import _ from 'lodash-es'

export const statusList = [
    {label: '禁用', value: '0'},
    {label: '正常', value: '1'},
]

export const formatDate = (datetime) => {
    return _.split(datetime, ' ')[0]
}

export const formatTimestamp = (timestamp) => {
    const date = new Date(Number(timestamp));
    const year = date.getFullYear();
    const month = String(date.getMonth() + 1).padStart(2, '0');
    const day = String(date.getDate()).padStart(2, '0');
    const hours = String(date.getHours()).padStart(2, '0');
    const minutes = String(date.getMinutes()).padStart(2, '0');
    const seconds = String(date.getSeconds()).padStart(2, '0');
    const milliseconds = String(date.getMilliseconds()).padStart(3, '0');
    return `${year}-${month}-${day} ${hours}:${minutes}:${seconds}.${milliseconds}`;
};