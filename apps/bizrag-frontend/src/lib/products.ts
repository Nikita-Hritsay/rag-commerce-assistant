import axios from './axios'

export type ProductDto = {
  id: string
  sku: string
  title: string
  description: string
  price?: number
  currency?: string
  quantity?: number
  updatedAt: string
}

export async function listAllProducts(): Promise<ProductDto[]> {
  const { data } = await axios.get('api/product') // без початкового '/'
  // Повертаємо завжди масив
  if (Array.isArray(data)) return data
  if (data && Array.isArray((data as any).content)) return (data as any).content
  return []
}

export async function patchProduct(id: string, delta: Partial<ProductDto>): Promise<ProductDto> {
  const { data } = await axios.patch(`api/product/${id}`, delta)
  return data
}
